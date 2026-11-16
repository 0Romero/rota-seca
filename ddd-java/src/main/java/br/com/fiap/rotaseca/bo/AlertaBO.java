package br.com.fiap.rotaseca.bo;

import br.com.fiap.rotaseca.dao.AlertaDAO;
import br.com.fiap.rotaseca.dao.LeituraChuvaDAO;
import br.com.fiap.rotaseca.dao.PontoApoioDAO;
import br.com.fiap.rotaseca.dao.PontoRiscoDAO;
import br.com.fiap.rotaseca.exception.RecursoNaoEncontradoException;
import br.com.fiap.rotaseca.exception.RegraNegocioException;
import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.Bairro;
import br.com.fiap.rotaseca.model.LeituraChuva;
import br.com.fiap.rotaseca.model.NivelRisco;
import br.com.fiap.rotaseca.model.PontoApoio;
import br.com.fiap.rotaseca.model.PontoRisco;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Regras de negócio dos alertas, inclusive a geração automática a partir da chuva. */
@Service
public class AlertaBO {

    /** Por quanto tempo um alerta gerado automaticamente fica em vigor. */
    static final int HORAS_VALIDADE = 3;

    private final AlertaDAO dao;
    private final LeituraChuvaDAO leituraDAO;
    private final PontoRiscoDAO pontoRiscoDAO;
    private final PontoApoioDAO pontoApoioDAO;
    private final BairroBO bairroBO;

    public AlertaBO(AlertaDAO dao, LeituraChuvaDAO leituraDAO, PontoRiscoDAO pontoRiscoDAO,
                    PontoApoioDAO pontoApoioDAO, BairroBO bairroBO) {
        this.dao = dao;
        this.leituraDAO = leituraDAO;
        this.pontoRiscoDAO = pontoRiscoDAO;
        this.pontoApoioDAO = pontoApoioDAO;
        this.bairroBO = bairroBO;
    }

    public List<Alerta> listar() {
        return dao.listar();
    }

    public List<Alerta> listarAtivos(Long bairroId) {
        if (bairroId != null) {
            bairroBO.buscarPorId(bairroId);
        }
        return dao.listarAtivos(LocalDateTime.now(), bairroId);
    }

    public Alerta buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }

    /** Alerta manual, emitido por um agente da Defesa Civil. */
    public Alerta cadastrar(Alerta alerta) {
        alerta.setId(null);
        if (alerta.getDataEmissao() == null) {
            alerta.setDataEmissao(LocalDateTime.now().withNano(0));
        }
        validar(alerta);
        return dao.inserir(alerta);
    }

    public Alerta atualizar(Long id, Alerta alerta) {
        Alerta atual = buscarPorId(id);
        alerta.setId(id);
        if (alerta.getDataEmissao() == null) {
            alerta.setDataEmissao(atual.getDataEmissao());
        }
        validar(alerta);
        dao.atualizar(alerta);
        return alerta;
    }

    public void excluir(Long id) {
        if (!dao.excluir(id)) {
            throw new RecursoNaoEncontradoException("Alerta", id);
        }
    }

    /**
     * Gera o alerta de uma leitura de chuva: diz o nível, quais trechos evitar
     * e para qual ponto de apoio ir. Chamado pelo LeituraChuvaBO.
     */
    public Alerta gerarParaLeitura(LeituraChuva leitura, NivelRisco nivel) {
        Bairro bairro = bairroBO.buscarPorId(leitura.getBairroId());
        LocalDateTime emissao = leitura.getDataHora();
        Alerta alerta = new Alerta(null, bairro.getId(), leitura.getId(), nivel,
                montarMensagem(bairro, leitura, nivel), emissao, emissao.plusHours(HORAS_VALIDADE));
        return dao.inserir(alerta);
    }

    private String montarMensagem(Bairro bairro, LeituraChuva leitura, NivelRisco nivel) {
        StringBuilder msg = new StringBuilder(String.format(Locale.forLanguageTag("pt-BR"),
                "Chuva %s em %s (%.1f mm/h).", descricao(nivel), bairro.getNome(), leitura.getPrecipitacaoMm()));

        String evitar = pontoRiscoDAO.listarPorBairro(bairro.getId()).stream()
                .sorted(Comparator.comparing(PontoRisco::getNivelRisco).reversed())
                .limit(2)
                .map(PontoRisco::getLocal)
                .collect(Collectors.joining("; "));
        if (!evitar.isEmpty()) {
            msg.append(" Evite: ").append(evitar).append('.');
        }

        List<PontoApoio> apoios = pontoApoioDAO.listarAtivosPorBairro(bairro.getId());
        if (!apoios.isEmpty()) {
            PontoApoio apoio = apoios.get(0);
            msg.append(" Ponto de apoio: ").append(apoio.getNome()).append(", ").append(apoio.getEndereco()).append('.');
        }
        if (nivel == NivelRisco.MUITO_ALTO) {
            msg.append(" Idosos e pessoas com mobilidade reduzida: saiam agora.");
        }
        return msg.length() <= 400 ? msg.toString() : msg.substring(0, 397) + "...";
    }

    private static String descricao(NivelRisco nivel) {
        return switch (nivel) {
            case BAIXO -> "fraca";
            case MODERADO -> "moderada";
            case ALTO -> "forte";
            case MUITO_ALTO -> "muito forte";
        };
    }

    private void validar(Alerta alerta) {
        bairroBO.validarExistencia(alerta.getBairroId());
        if (alerta.getLeituraId() != null && leituraDAO.buscarPorId(alerta.getLeituraId()).isEmpty()) {
            throw new RegraNegocioException("A leitura de chuva " + alerta.getLeituraId() + " não existe");
        }
        if (!alerta.getDataValidade().isAfter(alerta.getDataEmissao())) {
            throw new RegraNegocioException("A validade do alerta deve ser posterior à emissão");
        }
    }
}
