package br.com.fiap.streamfiap.model;

import jakarta.persistence.Entity;

@Entity
public class Serie extends Conteudo implements Promocionavel {

    private int numeroTemporadas;

    public Serie() {
    }

    // cria a série com os dados recebidos
    public Serie(String titulo, String categoria, int duracaoMinutos, int classificacaoEtaria, int numeroTemporadas, boolean disponivel) {
        super(titulo, categoria, duracaoMinutos, classificacaoEtaria, disponivel);
        this.setNumeroTemporadas(numeroTemporadas);
    }

    // preço da série: 4.90 por temporada
    public double calcularPrecoAluguel(double desconto) {
        return 4.90 * this.getNumeroTemporadas();
    }

    @Override
    public double aplicarPromocao(double preco) {
        return preco * 0.8;
    }

    public int getNumeroTemporadas() { return numeroTemporadas; }
    public void setNumeroTemporadas(int numeroTemporadas) {
        if (numeroTemporadas < 1) {
            throw new IllegalArgumentException("Séries devem ter ao mínimo 1 temporada");
        }
        this.numeroTemporadas = numeroTemporadas;
    }
}
