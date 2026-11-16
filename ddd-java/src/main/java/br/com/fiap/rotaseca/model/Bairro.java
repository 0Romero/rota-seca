package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Tabela RS_BAIRRO: bairro atendido pelo Rota Seca. */
public class Bairro {

    private Long id;

    @NotBlank(message = "O nome do bairro é obrigatório")
    @Size(max = 80)
    private String nome;

    @NotBlank(message = "A região é obrigatória")
    @Size(max = 30)
    private String regiao;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 60)
    private String cidade;

    @PositiveOrZero(message = "A população não pode ser negativa")
    private Integer populacao;

    public Bairro() {
    }

    public Bairro(Long id, String nome, String regiao, String cidade, Integer populacao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.cidade = cidade;
        this.populacao = populacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public Integer getPopulacao() { return populacao; }
    public void setPopulacao(Integer populacao) { this.populacao = populacao; }
}
