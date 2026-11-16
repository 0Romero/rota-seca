package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.LeituraChuvaDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.LeituraChuva;
import br.com.fiap.rotaseca.model.NivelRisco;
import br.com.fiap.rotaseca.model.RegistroLeitura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio das leituras de chuva. */
@Service
public class LeituraChuvaBO {

    /** Tolerância para diferenças de relógio entre o sensor e o servidor. */
    private static final int MINUTOS_TOLERANCIA_FUTURO = 5;

    private final LeituraChuvaDAO dao;
    private final BairroBO bairroBO;
    private final AlertaBO alertaBO;

    public LeituraChuvaBO(LeituraChuvaDAO dao, BairroBO bairroBO, AlertaBO alertaBO) {
        this.dao = dao;
        this.bairroBO = bairroBO;
        this.alertaBO = alertaBO;
    }

    public List<LeituraChuva> listar(Long bairroId) {
        if (bairroId == null) {
            return dao.listar();
        }
        bairroBO.buscarPorId(bairroId);
        return dao.listarPorBairro(bairroId);
    }

    public LeituraChuva buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Leitura de chuva", id));
    }

    /**
     * Registra a leitura e, se a chuva for de nível MODERADO ou maior, gera o alerta
     * para o bairro na mesma transação (ou grava os dois, ou nenhum).
     */
    @Transactional
    public RegistroLeitura registrar(LeituraChuva leitura) {
        leitura.setId(null);
        if (leitura.getDataHora() == null) {
            leitura.setDataHora(LocalDateTime.now().withNano(0));
        }
        validar(leitura);
        dao.inserir(leitura);

        NivelRisco nivel = leitura.calcularNivelRisco();
        Alerta alerta = nivel.geraAlerta() ? alertaBO.gerarParaLeitura(leitura, nivel) : null;
        return new RegistroLeitura(leitura, nivel, alerta);
    }

    /** Corrige uma leitura. Não gera um novo alerta: correções não devem disparar avisos duplicados. */
    public LeituraChuva atualizar(Long id, LeituraChuva leitura) {
        LeituraChuva atual = buscarPorId(id);
        leitura.setId(id);
        if (leitura.getDataHora() == null) {
            leitura.setDataHora(atual.getDataHora());
        }
        validar(leitura);
        dao.atualizar(leitura);
        return leitura;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Leitura de chuva", id);
        }
    }

    private void validar(LeituraChuva leitura) {
        bairroBO.validarExistencia(leitura.getBairroId());
        if (leitura.getDataHora().isAfter(LocalDateTime.now().plusMinutes(MINUTOS_TOLERANCIA_FUTURO))) {
            throw new RegraNegocioException("A data da leitura não pode estar no futuro");
        }
    }
}
