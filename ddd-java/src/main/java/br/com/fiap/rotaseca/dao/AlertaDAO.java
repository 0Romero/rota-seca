package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Alerta;
import br.com.fiap.rotaseca.model.NivelRisco;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_ALERTA. */
@Repository
public class AlertaDAO {

    private static final String SELECT = "SELECT ID_ALERTA, ID_BAIRRO, ID_LEITURA, DS_NIVEL, DS_MENSAGEM, DT_EMISSAO,"
            + " DT_VALIDADE FROM RS_ALERTA";

    private static final RowMapper<Alerta> MAPPER = (rs, i) -> new Alerta(
            rs.getLong("ID_ALERTA"),
            rs.getLong("ID_BAIRRO"),
            JdbcUtils.getLongOuNulo(rs, "ID_LEITURA"),
            NivelRisco.valueOf(rs.getString("DS_NIVEL")),
            rs.getString("DS_MENSAGEM"),
            JdbcUtils.getDataHora(rs, "DT_EMISSAO"),
            JdbcUtils.getDataHora(rs, "DT_VALIDADE"));

    private final JdbcTemplate jdbc;

    public AlertaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Alerta> listar() {
        return jdbc.query(SELECT + " ORDER BY DT_EMISSAO DESC", MAPPER);
    }

    /** Alertas em vigor no momento informado; se bairroId for nulo, traz de todos os bairros. */
    public List<Alerta> listarAtivos(LocalDateTime agora, Long bairroId) {
        String filtro = " WHERE DT_EMISSAO <= ? AND DT_VALIDADE > ?";
        if (bairroId == null) {
            return jdbc.query(SELECT + filtro + " ORDER BY DT_EMISSAO DESC", MAPPER,
                    JdbcUtils.timestamp(agora), JdbcUtils.timestamp(agora));
        }
        return jdbc.query(SELECT + filtro + " AND ID_BAIRRO = ? ORDER BY DT_EMISSAO DESC", MAPPER,
                JdbcUtils.timestamp(agora), JdbcUtils.timestamp(agora), bairroId);
    }

    public Optional<Alerta> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_ALERTA = ?", MAPPER, id).stream().findFirst();
    }

    public Alerta inserir(Alerta a) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_ALERTA (ID_BAIRRO, ID_LEITURA, DS_NIVEL, DS_MENSAGEM, DT_EMISSAO, DT_VALIDADE)"
                            + " VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_ALERTA"});
            ps.setLong(1, a.getBairroId());
            ps.setObject(2, a.getLeituraId(), Types.BIGINT);
            ps.setString(3, a.getNivel().name());
            ps.setString(4, a.getMensagem());
            ps.setTimestamp(5, JdbcUtils.timestamp(a.getDataEmissao()));
            ps.setTimestamp(6, JdbcUtils.timestamp(a.getDataValidade()));
            return ps;
        }, chave);
        a.setId(chave.getKey().longValue());
        return a;
    }

    public boolean atualizar(Alerta a) {
        return jdbc.update("UPDATE RS_ALERTA SET ID_BAIRRO = ?, ID_LEITURA = ?, DS_NIVEL = ?, DS_MENSAGEM = ?,"
                        + " DT_EMISSAO = ?, DT_VALIDADE = ? WHERE ID_ALERTA = ?",
                a.getBairroId(), a.getLeituraId(), a.getNivel().name(), a.getMensagem(),
                JdbcUtils.timestamp(a.getDataEmissao()), JdbcUtils.timestamp(a.getDataValidade()), a.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_ALERTA WHERE ID_ALERTA = ?", id) > 0;
    }
}
