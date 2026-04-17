package fabrica;

import java.util.concurrent.Semaphore;

public class EstacaoProducao {
    public static final int FUNCIONARIOS_POR_ESTACAO = 5;

    private final int id;
    private final Funcionario[] funcionarios;
    private final Semaphore[] ferramentas;

    public EstacaoProducao(int id, Fabrica fabrica) {
        this.id = id;
        this.ferramentas = new Semaphore[FUNCIONARIOS_POR_ESTACAO];
        this.funcionarios = new Funcionario[FUNCIONARIOS_POR_ESTACAO];

        for (int i = 0; i < FUNCIONARIOS_POR_ESTACAO; i++) {
            ferramentas[i] = new Semaphore(1);
        }
        for (int i = 0; i < FUNCIONARIOS_POR_ESTACAO; i++) {
            Semaphore esq = ferramentas[i];
            Semaphore dir = ferramentas[(i + 1) % FUNCIONARIOS_POR_ESTACAO];
            funcionarios[i] = new Funcionario(i, id, esq, dir, fabrica);
        }
    }

    public void iniciar() {
        for (Funcionario f : funcionarios) {
            f.start();
        }
    }

    public int getId() {
        return id;
    }
}
