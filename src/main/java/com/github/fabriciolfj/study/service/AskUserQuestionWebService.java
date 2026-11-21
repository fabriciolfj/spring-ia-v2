package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.model.Event;
import org.springaicommunity.agent.tools.AskUserQuestionTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


@Service
public class AskUserQuestionWebService {

    public enum Status { QUESTIONS, DONE, ERROR }


    private static final long TIMEOUT_MINUTES = 5;

    private static class Session {
        final BlockingQueue<Event> events = new LinkedBlockingQueue<>();
        volatile CompletableFuture<Map<String, String>> pendingAnswers;
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ChatClient chatClient;

    public AskUserQuestionWebService(ChatClient.Builder builder) {
        // build uma vez; a tool é passada por requisição (cada sessão tem seu handler)
        this.chatClient = builder.build();
    }

    public Event start(String userQuestion) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session();
        sessions.put(sessionId, session);

        AskUserQuestionTool tool = AskUserQuestionTool.builder()
                .questionHandler(questions -> waitForAnswers(sessionId, session, questions))
                .build();

        executor.submit(() -> {
            try {
                String answer = chatClient.prompt()
                        .user(userQuestion)
                        .tools(tool)
                        .call()
                        .content();
                session.events.add(new Event(sessionId, Status.DONE, null, answer));
            } catch (Exception e) {
                session.events.add(new Event(sessionId, Status.ERROR, null, e.getMessage()));
            }
        });

        return nextEvent(sessionId, session);
    }

    public Event answer(String sessionId, Map<String, String> answers) {
        Session session = sessions.get(sessionId);
        if (session == null || session.pendingAnswers == null) {
            throw new IllegalStateException("Nenhuma pergunta pendente para a sessão " + sessionId);
        }
        session.pendingAnswers.complete(answers);
        return nextEvent(sessionId, session);
    }

    /** Executado na thread do tool call: publica as perguntas e bloqueia até o front responder. */
    private Map<String, String> waitForAnswers(String sessionId,
                                               Session session,
                                               List<AskUserQuestionTool.Question> questions) {
        CompletableFuture<Map<String, String>> future = new CompletableFuture<>();
        session.pendingAnswers = future;
        session.events.add(new Event(sessionId, Status.QUESTIONS, questions, null));
        try {
            return future.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new IllegalStateException("Usuário não respondeu a tempo", e);
        } finally {
            session.pendingAnswers = null;
        }
    }

    private Event nextEvent(String sessionId, Session session) {
        try {
            Event event = session.events.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (event == null) {
                event = new Event(sessionId, Status.ERROR, null, "Timeout aguardando o modelo");
            }
            if (event.status() != Status.QUESTIONS) {
                sessions.remove(sessionId);
            }
            return event;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
