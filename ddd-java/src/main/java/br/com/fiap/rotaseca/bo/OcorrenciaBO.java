package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.MoradorDAO;
import br.com.fiap.rotaseca.dao.OcorrenciaDAO;
import br.com.fiap.rotaseca.dao.PontoRiscoDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.PontoRisco;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio das ocorrências de alagamento. */
@Service
public class OcorrenciaBO {

    private final OcorrenciaDAO dao;
    private final PontoRiscoDAO pontoRiscoDAO;
    private final MoradorDAO moradorDAO;
    private final PontoRiscoBO pontoRiscoBO;

    public OcorrenciaBO(OcorrenciaDAO dao, PontoRiscoDAO pontoRiscoDAO, MoradorDAO moradorDAO,
                        PontoRiscoBO pontoRiscoBO) {
        this.dao = dao;
        this.pontoRiscoDAO = pontoRiscoDAO;
        this.moradorDAO = moradorDAO;
        this.pontoRiscoBO = pontoRiscoBO;
    }

    public List<Ocorrencia> listar(StatusOcorrencia status) {
        return status == null ? dao.listar() : dao.listarPorStatus(status);
    }

    public Ocorrencia buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }

    /** Registra a ocorrência e atualiza a lâmina máxima do ponto de risco, se ela foi superada. */
    @Transactional
    public Ocorrencia registrar(Ocorrencia ocorrencia) {
        ocorrencia.setId(null);
        if (ocorrencia.getDataHora() == null) {
            ocorrencia.setDataHora(LocalDateTime.now().withNano(0));
        }
        if (ocorrencia.getStatus() == null) {
            ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        }
        PontoRisco ponto = validar(ocorrencia);
        dao.inserir(ocorrencia);
        pontoRiscoBO.registrarLamina(ponto, ocorrencia.getLaminaCm());
        return ocorrencia;
    }

    /** Atualiza a ocorrência respeitando o ciclo ABERTA -> EM_ATENDIMENTO -> ENCERRADA. */
    @Transactional
    public Ocorrencia atualizar(Long id, Ocorrencia ocorrencia) {
        Ocorrencia atual = buscarPorId(id);
        ocorrencia.setId(id);
        if (ocorrencia.getDataHora() == null) {
            ocorrencia.setDataHora(atual.getDataHora());
        }
        if (ocorrencia.getStatus() == null) {
            ocorrencia.setStatus(atual.getStatus());
        }
        if (!atual.getStatus().podeMudarPara(ocorrencia.getStatus())) {
            throw new RegraNegocioException("Não é possível mudar a ocorrência de "
                    + atual.getStatus() + " para " + ocorrencia.getStatus());
        }
        PontoRisco ponto = validar(ocorrencia);
        dao.atualizar(ocorrencia);
        pontoRiscoBO.registrarLamina(ponto, ocorrencia.getLaminaCm());
        return ocorrencia;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Ocorrência", id);
        }
    }

    private PontoRisco validar(Ocorrencia ocorrencia) {
        PontoRisco ponto = pontoRiscoDAO.buscarPorId(ocorrencia.getPontoRiscoId())
                .orElseThrow(() -> new RegraNegocioException(
                        "O ponto de risco " + ocorrencia.getPontoRiscoId() + " não existe"));
        if (ocorrencia.getMoradorId() != null && moradorDAO.buscarPorId(ocorrencia.getMoradorId()).isEmpty()) {
            throw new RegraNegocioException("O morador " + ocorrencia.getMoradorId() + " não existe");
        }
        if (ocorrencia.getDataHora().isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new RegraNegocioException("A data da ocorrência não pode estar no futuro");
        }
        return ponto;
    }
}
