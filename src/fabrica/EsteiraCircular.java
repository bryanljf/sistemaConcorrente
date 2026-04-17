package fabrica;

import java.util.concurrent.Semaphore;

public class EsteiraCircular {
    private final Veiculo[] buffer;
    private final int tamanho;
    private int entrada;
    private int saida;

    private final Semaphore mutex;
    private final Semaphore vagas;
    private final Semaphore itens;

    public EsteiraCircular(int tamanho) {
        this.tamanho = tamanho;
        this.buffer = new Veiculo[tamanho];
        this.entrada = 0;
        this.saida = 0;
        this.mutex = new Semaphore(1);
        this.vagas = new Semaphore(tamanho);
        this.itens = new Semaphore(0);
    }

    public int inserir(Veiculo v) throws InterruptedException {
        vagas.acquire();
        mutex.acquire();
        int posicao = entrada;
        buffer[entrada] = v;
        entrada = (entrada + 1) % tamanho;
        mutex.release();
        itens.release();
        return posicao;
    }

    public Veiculo retirar() throws InterruptedException {
        itens.acquire();
        mutex.acquire();
        Veiculo v = buffer[saida];
        buffer[saida] = null;
        saida = (saida + 1) % tamanho;
        mutex.release();
        vagas.release();
        return v;
    }

    public int getTamanho() {
        return tamanho;
    }
}
