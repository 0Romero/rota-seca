package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.PontoApoioDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.model.PontoApoio;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negócio dos pontos de apoio (abrigos, escolas, UBS). */
@Service
public class PontoApoioBO {

    private final PontoApoioDAO dao;
    private final BairroBO bairroBO;

    public PontoApoioBO(PontoApoioDAO dao, BairroBO bairroBO) {
        this.dao = dao;
        this.bairroBO = bairroBO;
    }

    public List<PontoApoio> listar() {
        return dao.listar();
    }

    /** Pontos de apoio abertos no bairro, do maior para o menor em capacidade. */
    public List<PontoApoio> listarAtivosPorBairro(Long bairroId) {
        bairroBO.buscarPorId(bairroId);
        return dao.listarAtivosPorBairro(bairroId);
    }

    public PontoApoio buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de apoio", id));
    }

    public PontoApoio cadastrar(PontoApoio ponto) {
        ponto.setId(null);
        if (ponto.getAtivo() == null) {
            ponto.setAtivo(true);
        }
        bairroBO.validarExistencia(ponto.getBairroId());
        return dao.inserir(ponto);
    }

    public PontoApoio atualizar(Long id, PontoApoio ponto) {
        PontoApoio atual = buscarPorId(id);
        bairroBO.validarExistencia(ponto.getBairroId());
        ponto.setId(id);
        if (ponto.getAtivo() == null) {
            ponto.setAtivo(atual.getAtivo());
        }
        dao.atualizar(ponto);
        return ponto;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Ponto de apoio", id);
        }
    }
}
