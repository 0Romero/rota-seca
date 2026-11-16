package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.MoradorDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.Morador;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negócio dos moradores. */
@Service
public class MoradorBO {

    private final MoradorDAO dao;
    private final BairroBO bairroBO;
    private final AlertaBO alertaBO;

    public MoradorBO(MoradorDAO dao, BairroBO bairroBO, AlertaBO alertaBO) {
        this.dao = dao;
        this.bairroBO = bairroBO;
        this.alertaBO = alertaBO;
    }

    public List<Morador> listar() {
        return dao.listar();
    }

    public Morador buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Morador", id));
    }

    /** Alertas em vigor agora no bairro do morador: é o que o app mostra na tela inicial. */
    public List<Alerta> alertasAtivos(Long moradorId) {
        Morador morador = buscarPorId(moradorId);
        return alertaBO.listarAtivos(morador.getBairroId());
    }

    public Morador cadastrar(Morador morador) {
        morador.setId(null);
        aplicarPadroes(morador);
        validar(morador);
        return dao.inserir(morador);
    }

    public Morador atualizar(Long id, Morador morador) {
        buscarPorId(id);
        morador.setId(id);
        aplicarPadroes(morador);
        validar(morador);
        dao.atualizar(morador);
        return morador;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Morador", id);
        }
    }

    private void aplicarPadroes(Morador morador) {
        if (morador.getMobilidadeReduzida() == null) {
            morador.setMobilidadeReduzida(false);
        }
        if (morador.getRecebeAlerta() == null) {
            morador.setRecebeAlerta(true);
        }
    }

    private void validar(Morador morador) {
        bairroBO.validarExistencia(morador.getBairroId());
        if (dao.existeTelefone(morador.getTelefone(), morador.getId())) {
            throw new RegraNegocioException("Já existe um morador com o telefone " + morador.getTelefone());
        }
    }
}
