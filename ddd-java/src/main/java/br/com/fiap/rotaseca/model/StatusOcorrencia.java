package br.com.fiap.rotaseca.model;

/** Ciclo de vida de uma ocorrencia: ABERTA -> EM_ATENDIMENTO -> ENCERRADA. */
public enum StatusOcorrencia {
    ABERTA,
    EM_ATENDIMENTO,
    ENCERRADA;

    public boolean podeMudarPara(StatusOcorrencia novo) {
        if (this == novo) {
            return true;
        }
        return switch (this) {
            case ABERTA -> novo == EM_ATENDIMENTO || novo == ENCERRADA;
            case EM_ATENDIMENTO -> novo == ENCERRADA;
            case ENCERRADA -> false;
        };
    }
}
