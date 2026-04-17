package fabrica;

import java.io.Serializable;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int id;
    public final String cor;
    public final String tipo;
    public final int estacaoId;
    public final int funcionarioId;
    public int posicaoEsteiraFabrica;
    public int lojaId = -1;
    public int posicaoEsteiraLoja = -1;

    public Veiculo(int id, String cor, String tipo, int estacaoId, int funcionarioId) {
        this.id = id;
        this.cor = cor;
        this.tipo = tipo;
        this.estacaoId = estacaoId;
        this.funcionarioId = funcionarioId;
    }

    @Override
    public String toString() {
        return "Veiculo[id=" + id + ", cor=" + cor + ", tipo=" + tipo
                + ", estacao=" + estacaoId + ", funcionario=" + funcionarioId + "]";
    }
}
