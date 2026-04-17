package cliente;

public class ClienteMain {
    public static final int NUM_CLIENTES = 20;

    public static void main(String[] args) throws InterruptedException {
        String[] hosts = {"localhost", "localhost", "localhost"};
        int[] portas = {6001, 6002, 6003};

        Cliente[] clientes = new Cliente[NUM_CLIENTES];
        for (int i = 0; i < NUM_CLIENTES; i++) {
            clientes[i] = new Cliente(i, hosts, portas);
        }

        for (Cliente c : clientes) {
            c.start();
            Thread.sleep(100);
        }

        for (Cliente c : clientes) {
            c.join();
        }

        System.out.println("Todos os clientes finalizaram suas compras.");
    }
}
