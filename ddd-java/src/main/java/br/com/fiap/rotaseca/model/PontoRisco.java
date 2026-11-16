package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Tabela RS_PONTO_RISCO: trecho de rua que costuma alagar. */
public class PontoRisco {

    private Long id;

    @NotNull(message = "O bairro é obrigatório")
    private Long bairroId;

    @NotBlank(message = "A descrição do local é obrigatória")
    @Size(max = 150)
    private String local;

    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @NotNull(message = "O nível de risco é obrigatório")
    private NivelRisco nivelRisco;

    /** Maior lâmina d'água já registrada no ponto, em centímetros. */
    @PositiveOrZero
    private Double laminaMaximaCm;

    public PontoRisco() {
    }

    public PontoRisco(Long id, Long bairroId, String local, Double latitude, Double longitude,
                      NivelRisco nivelRisco, Double laminaMaximaCm) {
        this.id = id;
        this.bairroId = bairroId;
        this.local = local;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nivelRisco = nivelRisco;
        this.laminaMaximaCm = laminaMaximaCm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public NivelRisco getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(NivelRisco nivelRisco) { this.nivelRisco = nivelRisco; }

    public Double getLaminaMaximaCm() { return laminaMaximaCm; }
    public void setLaminaMaximaCm(Double laminaMaximaCm) { this.laminaMaximaCm = laminaMaximaCm; }
}
