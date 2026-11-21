package com.github.fabriciolfj.study.model;

import java.util.concurrent.CompletableFuture;

public class EventoPagamento {

    private Pagamento pagamento;
    private CompletableFuture<Boolean> completableFuture;

    public EventoPagamento(Pagamento pagamento, CompletableFuture<Boolean> completable) {
        this.setPagamento(pagamento);
        this.setCompletableFuture(completable);
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public CompletableFuture<Boolean> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<Boolean> completableFuture) {
        this.completableFuture = completableFuture;
    }
}
