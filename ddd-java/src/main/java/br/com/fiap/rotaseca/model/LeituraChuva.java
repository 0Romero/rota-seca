package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Tabela RS_LEITURA_CHUVA: chuva acumulada em 1 hora medida em um bairro. */
public class LeituraChuva {

    private Long id;

    @NotNull(message = "O bairro é obrigatório")
    private Long bairroId;

    /** Se não for informada, o BO usa a data e hora atuais. */
    private LocalDateTime dataHora;

    @NotNull(message = "A precipitação é obrigatória")
    @PositiveOrZero(message = "A precipitação não pode ser negativa")
    private Double precipitacaoMm;

    @DecimalMin("0.0") @DecimalMax("100.0")
    private Double umidade;

    @NotBlank(message = "A fonte do dado é obrigatória")
    @Size(max = 60)
    private String fonte;

    public LeituraChuva() {
    }

    public LeituraChuva(Long id, Long bairroId, LocalDateTime dataHora, Double precipitacaoMm,
                        Double umidade, String fonte) {
        this.id = id;
        this.bairroId = bairroId;
        this.dataHora = dataHora;
        this.precipitacaoMm = precipitacaoMm;
        this.umidade = umidade;
        this.fonte = fonte;
    }

    public NivelRisco calcularNivelRisco() {
        return NivelRisco.porPrecipitacao(precipitacaoMm);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getPrecipitacaoMm() { return precipitacaoMm; }
    public void setPrecipitacaoMm(Double precipitacaoMm) { this.precipitacaoMm = precipitacaoMm; }

    public Double getUmidade() { return umidade; }
    public void setUmidade(Double umidade) { this.umidade = umidade; }

    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
}
