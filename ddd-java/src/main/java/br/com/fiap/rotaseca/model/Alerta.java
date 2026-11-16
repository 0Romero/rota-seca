package br.com.fiap.rotaseca.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Tabela RS_ALERTA: aviso de risco de alagamento enviado aos moradores de um bairro. */
public class Alerta {

    private Long id;

    @NotNull(message = "O bairro é obrigatório")
    private Long bairroId;

    /** Leitura de chuva que originou o alerta (nula em alertas manuais da Defesa Civil). */
    private Long leituraId;

    @NotNull(message = "O nível do alerta é obrigatório")
    private NivelRisco nivel;

    @NotBlank(message = "A mensagem é obrigatória")
    @Size(max = 400)
    private String mensagem;

    /** Se não for informada, o BO usa a data e hora atuais. */
    private LocalDateTime dataEmissao;

    @NotNull(message = "A validade é obrigatória")
    private LocalDateTime dataValidade;

    public Alerta() {
    }

    public Alerta(Long id, Long bairroId, Long leituraId, NivelRisco nivel, String mensagem,
                  LocalDateTime dataEmissao, LocalDateTime dataValidade) {
        this.id = id;
        this.bairroId = bairroId;
        this.leituraId = leituraId;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.dataEmissao = dataEmissao;
        this.dataValidade = dataValidade;
    }

    public boolean estaAtivo(LocalDateTime agora) {
        return !agora.isBefore(dataEmissao) && agora.isBefore(dataValidade);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public Long getLeituraId() { return leituraId; }
    public void setLeituraId(Long leituraId) { this.leituraId = leituraId; }

    public NivelRisco getNivel() { return nivel; }
    public void setNivel(NivelRisco nivel) { this.nivel = nivel; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }
}
