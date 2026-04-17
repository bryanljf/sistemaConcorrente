package fabrica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FabricaMain {
    public static final int PORTA_PADRAO = 5000;

    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : PORTA_PADRAO;

        Fabrica fabrica = new Fabrica();
        fabrica.iniciar();

        ServerSocket servidor = new ServerSocket(porta);
        System.out.println("Fabrica online na porta " + porta);

        while (true) {
            Socket socket = servidor.accept();
            Thread atendente = new Thread(() -> atender(socket, fabrica));
            atendente.start();
        }
    }

    private static void atender(Socket socket, Fabrica fabrica) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int lojaId = in.readInt();
            System.out.println("Loja " + lojaId + " conectada");

            while (!socket.isClosed()) {
                String comando = in.readUTF();
                if (!"PEDIR".equals(comando)) {
                    break;
                }
                Veiculo v = fabrica.entregarParaLoja();
                out.writeObject(v);
                out.reset();
                out.flush();

                int posLoja = in.readInt();
                v.lojaId = lojaId;
                v.posicaoEsteiraLoja = posLoja;
                fabrica.registrarVendaLoja(v, lojaId, posLoja);
            }
        } catch (Exception e) {
            System.out.println("Conexao com loja encerrada: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
