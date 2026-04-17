package fabrica;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Fabrica {
    public static final int CAPACIDADE_ESTOQUE = 500;
    public static final int LIMITE_ESTEIRA_ENTREGA = 5;
    public static final int CAPACIDADE_ESTEIRA_FABRICA = 40;
    public static final int NUM_ESTACOES = 4;
    public static final int LOTE_REPOSICAO = 100;
    public static final long INTERVALO_REPOSICAO_MS = 2000;

    private static final String[] CORES = {"R", "G", "B"};
    private static final String[] TIPOS = {"SUV", "SEDAN"};

    private final Semaphore estoquePecas;
    private final Semaphore esteiraEntrega;
    private final EsteiraCircular esteira;
    private final EstacaoProducao[] estacoes;
    private final AtomicInteger sequenciaId;

    private final Logger logProducao;
    private final Logger logVendaLoja;

    public Fabrica() throws IOException {
        this.estoquePecas = new Semaphore(CAPACIDADE_ESTOQUE);
        this.esteiraEntrega = new Semaphore(LIMITE_ESTEIRA_ENTREGA);
        this.esteira = new EsteiraCircular(CAPACIDADE_ESTEIRA_FABRICA);
        this.sequenciaId = new AtomicInteger(0);
        this.logProducao = new Logger("log_producao.txt", "PRODUCAO");
        this.logVendaLoja = new Logger("log_venda_loja.txt", "VENDA_LOJA");
        this.estacoes = new EstacaoProducao[NUM_ESTACOES];
        for (int i = 0; i < NUM_ESTACOES; i++) {
            estacoes[i] = new EstacaoProducao(i, this);
        }
    }

    public void iniciar() {
        for (EstacaoProducao e : estacoes) e.iniciar();
        iniciarRepositorPecas();
    }

    private void iniciarRepositorPecas() {
        Thread repositor = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(INTERVALO_REPOSICAO_MS);
                    int disponivel = estoquePecas.availablePermits();
                    int repor = Math.min(LOTE_REPOSICAO, CAPACIDADE_ESTOQUE - disponivel);
                    if (repor > 0) estoquePecas.release(repor);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RepositorPecas");
        repositor.setDaemon(true);
        repositor.start();
    }

    public void consumirPecas(int qtd) throws InterruptedException {
        estoquePecas.acquire(qtd);
    }

    public Veiculo montarVeiculo(int estacaoId, int funcionarioId) {
        int id = sequenciaId.getAndIncrement();
        String cor = CORES[id % CORES.length];
        String tipo = TIPOS[id % TIPOS.length];
        return new Veiculo(id, cor, tipo, estacaoId, funcionarioId);
    }

    public void depositarVeiculo(Veiculo v) throws InterruptedException {
        int posicao = esteira.inserir(v);
        v.posicaoEsteiraFabrica = posicao;
        logProducao.escrever(String.format(
                "id=%d cor=%s tipo=%s estacao=%d funcionario=%d pos_esteira=%d",
                v.id, v.cor, v.tipo, v.estacaoId, v.funcionarioId, posicao));
    }

    public Veiculo entregarParaLoja() throws InterruptedException {
        esteiraEntrega.acquire();
        try {
            return esteira.retirar();
        } finally {
            esteiraEntrega.release();
        }
    }

    public void registrarVendaLoja(Veiculo v, int lojaId, int posicaoLoja) {
        logVendaLoja.escrever(String.format(
                "id=%d cor=%s tipo=%s estacao=%d funcionario=%d pos_fabrica=%d loja=%d pos_loja=%d",
                v.id, v.cor, v.tipo, v.estacaoId, v.funcionarioId,
                v.posicaoEsteiraFabrica, lojaId, posicaoLoja));
    }
}
