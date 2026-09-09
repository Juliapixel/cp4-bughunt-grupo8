package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private Long id;

    private String nome;
    private int idade;
    private double creditos;

    public Usuario() {
    }

    public Usuario(String nome, int idade, double creditos) {
        this.setNome(nome);
        this.setIdade(idade);
        this.setCreditos(creditos);
    }

    public boolean temCreditosSuficientes(double preco) {
        return preco <= this.getCreditos();
    }

    // subtrai o valor dos créditos do usuário
    public void debitarCreditos(double valor) {
        this.setCreditos(this.getCreditos() - valor);
    }

    public Usuario alugar(Conteudo c) throws ClassificacaoIndicativaException {
        if (!c.isDisponivel()) {
            throw new ConteudoIndisponivelException("Conteúdo " + c.getTitulo() + " indisponível");
        }

        if (this.getIdade() < c.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException("Usuário de " + this.getIdade()
                    + " anos não pode assistir a " + c.getTitulo()
                    + " (classificação " + c.getClassificacaoEtaria() + " anos)");
        }

        double p = c.calcularPrecoPromocional();

        if (!temCreditosSuficientes(p)) {
            throw new CreditosInsuficientesException("Créditos insuficientes para alugar " + c.getTitulo());
        }

        debitarCreditos(p);
        c.setDisponivel(false);

        System.out.println("==================================================");
        System.out.println("RECIBO STREAMFIAP");
        System.out.println("Usuario: " + this.getNome());
        System.out.println("Conteudo: " + c.getTitulo());
        System.out.println("Valor pago: R$ " + p);
        System.out.println("Creditos restantes: R$ " + this.getCreditos());
        System.out.println("Obrigado por usar o StreamFIAP!");
        System.out.println("==================================================");

        return this;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) {
        if (idade < 0) {
            throw new IllegalArgumentException("idade não pode ser negativa");
        }
        this.idade = idade;
    }

    public double getCreditos() { return creditos; }
    public void setCreditos(double creditos) {
        if (!Double.isFinite(creditos)) {
            throw new IllegalArgumentException("O usuário deve ter um valor finito e numérico de créditos");
        }
        this.creditos = creditos;
    }
}
