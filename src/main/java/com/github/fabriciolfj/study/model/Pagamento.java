package com.github.fabriciolfj.study.model;

import java.math.BigDecimal;

public class Pagamento {

    private boolean aprovado;
    private BigDecimal valor;

    public Pagamento(BigDecimal valor, boolean isProvado) {
        this.valor = valor;
        this.aprovado = isProvado;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Pagamento{" +
                "aprovado=" + aprovado +
                ", valor=" + valor +
                '}';
    }
}