package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Ocorrencia;
import br.com.fiap.rotaseca.model.StatusOcorrencia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_OCORRENCIA. */
@Repository
public class OcorrenciaDAO {

    private static final String SELECT = "SELECT ID_OCORRENCIA, ID_PONTO_RISCO, ID_MORADOR, DT_OCORRENCIA,"
            + " NR_LAMINA_CM, DS_OBSERVACAO, DS_STATUS FROM RS_OCORRENCIA";

    private static final RowMapper<Ocorrencia> MAPPER = (rs, i) -> new Ocorrencia(
            rs.getLong("ID_OCORRENCIA"),
            rs.getLong("ID_PONTO_RISCO"),
            JdbcUtils.getLongOuNulo(rs, "ID_MORADOR"),
            JdbcUtils.getDataHora(rs, "DT_OCORRENCIA"),
            rs.getDouble("NR_LAMINA_CM"),
            rs.getString("DS_OBSERVACAO"),
            StatusOcorrencia.valueOf(rs.getString("DS_STATUS")));

    private final JdbcTemplate jdbc;

    public OcorrenciaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Ocorrencia> listar() {
        return jdbc.query(SELECT + " ORDER BY DT_OCORRENCIA DESC", MAPPER);
    }

    public List<Ocorrencia> listarPorStatus(StatusOcorrencia status) {
        return jdbc.query(SELECT + " WHERE DS_STATUS = ? ORDER BY DT_OCORRENCIA DESC", MAPPER, status.name());
    }

    public Optional<Ocorrencia> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_OCORRENCIA = ?", MAPPER, id).stream().findFirst();
    }

    public Ocorrencia inserir(Ocorrencia o) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_OCORRENCIA (ID_PONTO_RISCO, ID_MORADOR, DT_OCORRENCIA, NR_LAMINA_CM,"
                            + " DS_OBSERVACAO, DS_STATUS) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_OCORRENCIA"});
            ps.setLong(1, o.getPontoRiscoId());
            ps.setObject(2, o.getMoradorId(), Types.BIGINT);
            ps.setTimestamp(3, JdbcUtils.timestamp(o.getDataHora()));
            ps.setDouble(4, o.getLaminaCm());
            ps.setString(5, o.getObservacao());
            ps.setString(6, o.getStatus().name());
            return ps;
        }, chave);
        o.setId(chave.getKey().longValue());
        return o;
    }

    public boolean atualizar(Ocorrencia o) {
        return jdbc.update("UPDATE RS_OCORRENCIA SET ID_PONTO_RISCO = ?, ID_MORADOR = ?, DT_OCORRENCIA = ?,"
                        + " NR_LAMINA_CM = ?, DS_OBSERVACAO = ?, DS_STATUS = ? WHERE ID_OCORRENCIA = ?",
                o.getPontoRiscoId(), o.getMoradorId(), JdbcUtils.timestamp(o.getDataHora()), o.getLaminaCm(),
                o.getObservacao(), o.getStatus().name(), o.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_OCORRENCIA WHERE ID_OCORRENCIA = ?", id) > 0;
    }
}
