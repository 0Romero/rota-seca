package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.PontoRiscoDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.model.PontoRisco;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negócio dos pontos de risco (trechos que alagam). */
@Service
public class PontoRiscoBO {

    private final PontoRiscoDAO dao;
    private final BairroBO bairroBO;

    public PontoRiscoBO(PontoRiscoDAO dao, BairroBO bairroBO) {
        this.dao = dao;
        this.bairroBO = bairroBO;
    }

    public List<PontoRisco> listar(Long bairroId) {
        if (bairroId == null) {
            return dao.listar();
        }
        bairroBO.buscarPorId(bairroId);
        return dao.listarPorBairro(bairroId);
    }

    public PontoRisco buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de risco", id));
    }

    public PontoRisco cadastrar(PontoRisco ponto) {
        ponto.setId(null);
        bairroBO.validarExistencia(ponto.getBairroId());
        return dao.inserir(ponto);
    }

    public PontoRisco atualizar(Long id, PontoRisco ponto) {
        buscarPorId(id);
        bairroBO.validarExistencia(ponto.getBairroId());
        ponto.setId(id);
        dao.atualizar(ponto);
        return ponto;
    }

    /**
     * Chamado quando uma ocorrência registra uma lâmina d'água maior que a já conhecida:
     * o ponto "aprende" com o histórico e passa a ter o novo máximo.
     */
    public void registrarLamina(PontoRisco ponto, double laminaCm) {
        if (ponto.getLaminaMaximaCm() == null || laminaCm > ponto.getLaminaMaximaCm()) {
            dao.atualizarLaminaMaxima(ponto.getId(), laminaCm);
        }
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Ponto de risco", id);
        }
    }
}
