package com.github.fabriciolfj.study.listener;

import com.github.fabriciolfj.study.model.TodoUpdateEvent;
import org.springaicommunity.agent.tools.TodoWriteTool;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TodoProgressListener {

    @EventListener
    void onUpdate(TodoUpdateEvent e) {
        long done = e.todos().stream()
                .filter(t -> t.status() == TodoWriteTool.Todos.Status.completed)
                .count();
        System.out.printf("Progresso: %d/%d (%.0f%%)%n",
                done, e.todos().size(), done * 100.0 / e.todos().size());
    }
}
