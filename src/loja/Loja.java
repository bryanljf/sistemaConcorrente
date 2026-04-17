package loja;

import fabrica.EsteiraCircular;
import fabrica.Logger;
import fabrica.Veiculo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Loja {
    public static final int CAPACIDADE_ESTEIRA = 20;

    private final int id;
    private final EsteiraCircular esteira;
    private final Socket socketFabrica;
    private final ObjectOutputStream outFabrica;
    private final ObjectInputStream inFabrica;
    private final Semaphore canalFabrica;
    private final Logger logRecebimento;
    private final Logger logVendaCliente;

    public Loja(int id, String hostFabrica, int portaFabrica) throws IOException {
        this.id = id;
        this.esteira = new EsteiraCircular(CAPACIDADE_ESTEIRA);
        this.canalFabrica = new Semaphore(1);

        this.socketFabrica = new Socket(hostFabrica, portaFabrica);
        this.outFabrica = new ObjectOutputStream(socketFabrica.getOutputStream());
        this.outFabrica.flush();
        this.inFabrica = new ObjectInputStream(socketFabrica.getInputStream());
        this.outFabrica.writeInt(id);
        this.outFabrica.flush();

        this.logRecebimento = new Logger("loja_" + id + "_recebimento.txt", "RECEBIDO_L" + id);
        this.logVendaCliente = new Logger("loja_" + id + "_venda_cliente.txt", "VENDA_CLI_L" + id);
    }

    public void iniciarRepositor() {
        Thread t = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Veiculo v = pedirVeiculo();
                    int pos = esteira.inserir(v);
                    v.lojaId = id;
                    v.posicaoEsteiraLoja = pos;
                    confirmarPosicao(pos);
                    logRecebimento.escrever(String.format(
                            "id=%d cor=%s tipo=%s estacao=%d funcionario=%d pos_fabrica=%d pos_loja=%d",
                            v.id, v.cor, v.tipo, v.estacaoId, v.funcionarioId,
                            v.posicaoEsteiraFabrica, pos));
                }
            } catch (Exception e) {
                System.err.println("Repositor da loja " + id + " encerrado: " + e.getMessage());
            }
        }, "RepositorLoja-" + id);
        t.start();
    }

    private Veiculo pedirVeiculo() throws IOException, ClassNotFoundException, InterruptedException {
        canalFabrica.acquire();
        outFabrica.writeUTF("PEDIR");
        outFabrica.flush();
        return (Veiculo) inFabrica.readObject();
    }

    private void confirmarPosicao(int pos) throws IOException {
        try {
            outFabrica.writeInt(pos);
            outFabrica.flush();
        } finally {
            canalFabrica.release();
        }
    }

    public Veiculo venderVeiculo(int clienteId) throws InterruptedException {
        Veiculo v = esteira.retirar();
        logVendaCliente.escrever(String.format(
                "id=%d cor=%s tipo=%s estacao=%d funcionario=%d pos_loja=%d cliente=%d",
                v.id, v.cor, v.tipo, v.estacaoId, v.funcionarioId, v.posicaoEsteiraLoja, clienteId));
        return v;
    }

    public int getId() {
        return id;
    }
}
