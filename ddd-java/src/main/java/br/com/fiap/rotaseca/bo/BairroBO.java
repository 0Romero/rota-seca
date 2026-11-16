package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.BairroDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Bairro;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negócio de bairros. */
@Service
public class BairroBO {

    private final BairroDAO dao;

    public BairroBO(BairroDAO dao) {
        this.dao = dao;
    }

    public List<Bairro> listar() {
        return dao.listar();
    }

    public Bairro buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Bairro", id));
    }

    /** Usado pelos outros BOs para validar o bairro informado no corpo da requisição. */
    public void validarExistencia(Long id) {
        if (!dao.existe(id)) {
            throw new RegraNegocioException("O bairro " + id + " não existe");
        }
    }

    public Bairro cadastrar(Bairro bairro) {
        bairro.setId(null);
        validarNomeUnico(bairro);
        return dao.inserir(bairro);
    }

    public Bairro atualizar(Long id, Bairro bairro) {
        buscarPorId(id);
        bairro.setId(id);
        validarNomeUnico(bairro);
        dao.atualizar(bairro);
        return bairro;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Bairro", id);
        }
    }

    private void validarNomeUnico(Bairro bairro) {
        if (dao.existeComNome(bairro.getNome(), bairro.getCidade(), bairro.getId())) {
            throw new RegraNegocioException("Já existe o bairro " + bairro.getNome() + " em " + bairro.getCidade());
        }
    }
}
