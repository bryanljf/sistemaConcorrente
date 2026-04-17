package cliente;

import fabrica.Veiculo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Cliente extends Thread {
    public static final int CAPACIDADE_GARAGEM = 10;

    private final int id;
    private final String[] hostsLojas;
    private final int[] portasLojas;
    private final Veiculo[] garagem;
    private int ocupacaoGaragem;
    private final Semaphore mutexGaragem;
    private final Random rand;

    public Cliente(int id, String[] hostsLojas, int[] portasLojas) {
        this.id = id;
        this.hostsLojas = hostsLojas;
        this.portasLojas = portasLojas;
        this.garagem = new Veiculo[CAPACIDADE_GARAGEM];
        this.ocupacaoGaragem = 0;
        this.mutexGaragem = new Semaphore(1);
        this.rand = new Random();
        setName("Cliente-" + id);
    }

    @Override
    public void run() {
        int quantidade = 1 + rand.nextInt(5);
        for (int i = 0; i < quantidade; i++) {
            int escolha = rand.nextInt(hostsLojas.length);
            try {
                Veiculo v = comprarNaLoja(escolha);
                guardarNaGaragem(v);
                System.out.printf("[CLIENTE %d] comprou veiculo id=%d (%s %s) na loja %d%n",
                        id, v.id, v.cor, v.tipo, escolha);
                Thread.sleep(400 + rand.nextInt(1000));
            } catch (Exception e) {
                System.err.println("Cliente " + id + " falhou: " + e.getMessage());
            }
        }
    }

    private Veiculo comprarNaLoja(int indice) throws Exception {
        try (Socket socket = new Socket(hostsLojas[indice], portasLojas[indice])) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeInt(id);
            out.flush();
            return (Veiculo) in.readObject();
        }
    }

    private void guardarNaGaragem(Veiculo v) throws InterruptedException {
        mutexGaragem.acquire();
        try {
            if (ocupacaoGaragem < garagem.length) {
                garagem[ocupacaoGaragem++] = v;
            }
        } finally {
            mutexGaragem.release();
        }
    }

    public int getClienteId() {
        return id;
    }
}
