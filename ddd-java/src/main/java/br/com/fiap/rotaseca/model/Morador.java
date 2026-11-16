package br.com.fiap.rotaseca.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.Period;

/** Tabela RS_MORADOR: morador cadastrado para receber alertas (dados simulados). */
public class Morador {

    private Long id;

    @NotNull(message = "O bairro é obrigatório")
    private Long bairroId;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Informe o telefone com DDD, só números (10 ou 11 dígitos)")
    private String telefone;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    private Boolean mobilidadeReduzida = false;

    private Boolean recebeAlerta = true;

    public Morador() {
    }

    public Morador(Long id, Long bairroId, String nome, String telefone, LocalDate dataNascimento,
                   Boolean mobilidadeReduzida, Boolean recebeAlerta) {
        this.id = id;
        this.bairroId = bairroId;
        this.nome = nome;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.mobilidadeReduzida = mobilidadeReduzida;
        this.recebeAlerta = recebeAlerta;
    }

    /** Idosos (60+) e pessoas com mobilidade reduzida precisam sair mais cedo quando há alerta. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isPrioritario() {
        boolean idoso = dataNascimento != null
                && Period.between(dataNascimento, LocalDate.now()).getYears() >= 60;
        return idoso || Boolean.TRUE.equals(mobilidadeReduzida);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Boolean getMobilidadeReduzida() { return mobilidadeReduzida; }
    public void setMobilidadeReduzida(Boolean mobilidadeReduzida) { this.mobilidadeReduzida = mobilidadeReduzida; }

    public Boolean getRecebeAlerta() { return recebeAlerta; }
    public void setRecebeAlerta(Boolean recebeAlerta) { this.recebeAlerta = recebeAlerta; }
}
