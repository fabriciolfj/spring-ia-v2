package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.model.EventoPagamento;
import com.github.fabriciolfj.study.model.Pagamento;

import java.math.BigDecimal;
import java.util.concurrent.*;

public class SimulacaoCompletableFuture {

    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private static BlockingQueue<EventoPagamento> EVENTO_PAGAMENTO = new LinkedBlockingDeque<>();

    private EventoPagamento eventoPagamento;

    public SimulacaoCompletableFuture(EventoPagamento pagamento) {
        this.eventoPagamento = pagamento;
    }

    static void main() throws InterruptedException {
        var pagamento = new Pagamento(BigDecimal.valueOf(1000), false);
        var completable = new CompletableFuture<Boolean>();

        var evento = new EventoPagamento(pagamento, completable);
        var simulacao = new SimulacaoCompletableFuture(evento);
        simulacao.process();

        var result = EVENTO_PAGAMENTO.poll(10, TimeUnit.SECONDS);
        result.getCompletableFuture().complete(true);

        IO.println(result.getPagamento());

    }

    void process() {

        executor.submit(() -> {
            try {
                aprovacao();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
    };

    void aprovacao() throws InterruptedException {
        eventoPagamento.getPagamento().setAprovado(eventoPagamento.getPagamento().getValor().compareTo(BigDecimal.valueOf(100)) >= 0);
        processarPagamento();
    }

    private void processarPagamento() {
        CompletableFuture completableFuture = new CompletableFuture<Boolean>();
        eventoPagamento.setCompletableFuture(completableFuture);
        EVENTO_PAGAMENTO.add(this.eventoPagamento);

        IO.println("aguardando aprovacao");

        try {
            eventoPagamento.getCompletableFuture().get(4, TimeUnit.SECONDS);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
