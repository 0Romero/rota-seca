package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Tabela RS_OCORRENCIA: alagamento observado em um ponto de risco. */
public class Ocorrencia {

    private Long id;

    @NotNull(message = "O ponto de risco é obrigatório")
    private Long pontoRiscoId;

    /** Morador que reportou (nulo quando o registro é anônimo). */
    private Long moradorId;

    /** Se não for informada, o BO usa a data e hora atuais. */
    private LocalDateTime dataHora;

    @NotNull(message = "A lâmina d'água é obrigatória")
    @PositiveOrZero(message = "A lâmina d'água não pode ser negativa")
    private Double laminaCm;

    @Size(max = 300)
    private String observacao;

    /** Se não for informado, o BO usa ABERTA. */
    private StatusOcorrencia status;

    public Ocorrencia() {
    }

    public Ocorrencia(Long id, Long pontoRiscoId, Long moradorId, LocalDateTime dataHora, Double laminaCm,
                      String observacao, StatusOcorrencia status) {
        this.id = id;
        this.pontoRiscoId = pontoRiscoId;
        this.moradorId = moradorId;
        this.dataHora = dataHora;
        this.laminaCm = laminaCm;
        this.observacao = observacao;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPontoRiscoId() { return pontoRiscoId; }
    public void setPontoRiscoId(Long pontoRiscoId) { this.pontoRiscoId = pontoRiscoId; }

    public Long getMoradorId() { return moradorId; }
    public void setMoradorId(Long moradorId) { this.moradorId = moradorId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getLaminaCm() { return laminaCm; }
    public void setLaminaCm(Double laminaCm) { this.laminaCm = laminaCm; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
}
