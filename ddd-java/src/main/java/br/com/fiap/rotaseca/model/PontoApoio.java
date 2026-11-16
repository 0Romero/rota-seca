package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Tabela RS_PONTO_APOIO: abrigo, escola ou UBS para onde o morador pode ir. */
public class PontoApoio {

    private Long id;

    @NotNull(message = "O bairro é obrigatório")
    private Long bairroId;

    @NotBlank(message = "O nome do ponto de apoio é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotNull(message = "O tipo é obrigatório")
    private TipoPontoApoio tipo;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 150)
    private String endereco;

    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @NotNull @Positive(message = "A capacidade deve ser maior que zero")
    private Integer capacidade;

    private Boolean ativo = true;

    public PontoApoio() {
    }

    public PontoApoio(Long id, Long bairroId, String nome, TipoPontoApoio tipo, String endereco,
                      Double latitude, Double longitude, Integer capacidade, Boolean ativo) {
        this.id = id;
        this.bairroId = bairroId;
        this.nome = nome;
        this.tipo = tipo;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacidade = capacidade;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoPontoApoio getTipo() { return tipo; }
    public void setTipo(TipoPontoApoio tipo) { this.tipo = tipo; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
