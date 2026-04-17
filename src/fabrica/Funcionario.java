package fabrica;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Funcionario extends Thread {
    private static final int PECAS_POR_VEICULO = 2;

    private final int id;
    private final int estacaoId;
    private final Semaphore ferramentaEsq;
    private final Semaphore ferramentaDir;
    private final Fabrica fabrica;
    private final Random rand;

    public Funcionario(int id, int estacaoId, Semaphore ferramentaEsq,
                       Semaphore ferramentaDir, Fabrica fabrica) {
        this.id = id;
        this.estacaoId = estacaoId;
        this.ferramentaEsq = ferramentaEsq;
        this.ferramentaDir = ferramentaDir;
        this.fabrica = fabrica;
        this.rand = new Random();
        setName("Funcionario-" + estacaoId + "-" + id);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                fabrica.consumirPecas(PECAS_POR_VEICULO);
                pegarFerramentas();
                Veiculo v = fabrica.montarVeiculo(estacaoId, id);
                Thread.sleep(300 + rand.nextInt(400));
                soltarFerramentas();
                fabrica.depositarVeiculo(v);
                Thread.sleep(rand.nextInt(200));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void pegarFerramentas() throws InterruptedException {
        if (id % 2 == 0) {
            ferramentaEsq.acquire();
            ferramentaDir.acquire();
        } else {
            ferramentaDir.acquire();
            ferramentaEsq.acquire();
        }
    }

    private void soltarFerramentas() {
        ferramentaDir.release();
        ferramentaEsq.release();
    }
}
