package com.github.fabriciolfj.study.service;

import java.util.concurrent.*;


public class AprovacaoPedido {

    enum Tipo { PRECISA_APROVACAO, FINALIZADO }
    record Evento(Tipo tipo, String mensagem) {}


    static final BlockingQueue<Evento> eventos = new LinkedBlockingQueue<>();
    static volatile CompletableFuture<Boolean> aprovacaoPendente;

    static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        executor.submit(() -> processarPedido(1500.0));

        // 2. thread principal espera o PRIMEIRO evento (como o nextEvent())
        Evento e1 = eventos.poll(5, TimeUnit.SECONDS);
        log("main", "recebi evento: " + e1);

        // 3. simula o gerente pensando e respondendo (como o POST /answers)
        Thread.sleep(2000);
        log("main", "gerente aprovou -> complete(true)");
        aprovacaoPendente.complete(true);

        // 4. espera o próximo evento
        Evento e2 = eventos.poll(5, TimeUnit.SECONDS);
        log("main", "recebi evento: " + e2);

        executor.shutdown();
    }


    static void processarPedido(double valor) {
        log("worker", "processando pedido de R$ " + valor);

        if (valor > 1000) {
            boolean aprovado = aguardarAprovacao("Pedido de R$ " + valor + " precisa de aprovação");
            if (!aprovado) {
                eventos.add(new Evento(Tipo.FINALIZADO, "Pedido recusado"));
                return;
            }
        }

        log("worker", "pagamento efetuado");
        eventos.add(new Evento(Tipo.FINALIZADO, "Pedido concluído"));
    }

    // equivalente ao waitForAnswers
    static boolean aguardarAprovacao(String motivo) {
        CompletableFuture<Boolean> future = new CompletableFuture<>(); // caixa vazia
        aprovacaoPendente = future;                                    // deixa no "balcão"
        eventos.add(new Evento(Tipo.PRECISA_APROVACAO, motivo));        // avisa quem está fora
        try {
            log("worker", "travado no get() esperando aprovação...");
            Boolean resultado = future.get(10, TimeUnit.SECONDS);      // TRAVA
            log("worker", "destravou! aprovado=" + resultado);
            return resultado;
        } catch (TimeoutException e) {
            log("worker", "ninguém aprovou a tempo");
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            aprovacaoPendente = null;
        }
    }

    static void log(String quem, String msg) {
        System.out.printf("[%tT] %-6s | %s%n", System.currentTimeMillis(), quem, msg);
    }
}
