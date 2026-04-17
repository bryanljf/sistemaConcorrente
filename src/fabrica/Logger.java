package fabrica;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class Logger {
    private final PrintWriter saida;
    private final String prefixo;
    private final Semaphore mutex;

    public Logger(String arquivo, String prefixo) throws IOException {
        this.saida = new PrintWriter(new FileWriter(arquivo, false), true);
        this.prefixo = prefixo;
        this.mutex = new Semaphore(1);
    }

    public void escrever(String mensagem) {
        try {
            mutex.acquire();
            String linha = "[" + prefixo + "] " + mensagem;
            saida.println(linha);
            System.out.println(linha);
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void fechar() {
        saida.close();
    }
}
