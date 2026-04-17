package loja;

import fabrica.Veiculo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LojaMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Uso: java loja.LojaMain <id> <portaLoja> <hostFabrica> <portaFabrica>");
            return;
        }

        int id = Integer.parseInt(args[0]);
        int portaLoja = Integer.parseInt(args[1]);
        String hostFabrica = args[2];
        int portaFabrica = Integer.parseInt(args[3]);

        Loja loja = new Loja(id, hostFabrica, portaFabrica);
        loja.iniciarRepositor();

        ServerSocket servidor = new ServerSocket(portaLoja);
        System.out.println("Loja " + id + " online na porta " + portaLoja);

        while (true) {
            Socket cliente = servidor.accept();
            Thread t = new Thread(() -> atender(cliente, loja));
            t.start();
        }
    }

    private static void atender(Socket socket, Loja loja) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                int clienteId = in.readInt();
                Veiculo v = loja.venderVeiculo(clienteId);
                out.writeObject(v);
                out.reset();
                out.flush();
            }
        } catch (Exception e) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
