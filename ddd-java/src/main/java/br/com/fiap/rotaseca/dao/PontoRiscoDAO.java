package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.NivelRisco;
import br.com.fiap.rotaseca.model.PontoRisco;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_PONTO_RISCO. */
@Repository
public class PontoRiscoDAO {

    private static final String SELECT = "SELECT ID_PONTO_RISCO, ID_BAIRRO, DS_LOCAL, NR_LATITUDE, NR_LONGITUDE,"
            + " DS_NIVEL_RISCO, NR_LAMINA_MAX_CM FROM RS_PONTO_RISCO";

    private static final RowMapper<PontoRisco> MAPPER = (rs, i) -> new PontoRisco(
            rs.getLong("ID_PONTO_RISCO"),
            rs.getLong("ID_BAIRRO"),
            rs.getString("DS_LOCAL"),
            rs.getDouble("NR_LATITUDE"),
            rs.getDouble("NR_LONGITUDE"),
            NivelRisco.valueOf(rs.getString("DS_NIVEL_RISCO")),
            JdbcUtils.getDoubleOuNulo(rs, "NR_LAMINA_MAX_CM"));

    private final JdbcTemplate jdbc;

    public PontoRiscoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PontoRisco> listar() {
        return jdbc.query(SELECT + " ORDER BY ID_PONTO_RISCO", MAPPER);
    }

    public List<PontoRisco> listarPorBairro(Long bairroId) {
        return jdbc.query(SELECT + " WHERE ID_BAIRRO = ? ORDER BY ID_PONTO_RISCO", MAPPER, bairroId);
    }

    public Optional<PontoRisco> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_PONTO_RISCO = ?", MAPPER, id).stream().findFirst();
    }

    public PontoRisco inserir(PontoRisco p) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_PONTO_RISCO (ID_BAIRRO, DS_LOCAL, NR_LATITUDE, NR_LONGITUDE, DS_NIVEL_RISCO,"
                            + " NR_LAMINA_MAX_CM) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_PONTO_RISCO"});
            ps.setLong(1, p.getBairroId());
            ps.setString(2, p.getLocal());
            ps.setDouble(3, p.getLatitude());
            ps.setDouble(4, p.getLongitude());
            ps.setString(5, p.getNivelRisco().name());
            ps.setObject(6, p.getLaminaMaximaCm(), Types.DOUBLE);
            return ps;
        }, chave);
        p.setId(chave.getKey().longValue());
        return p;
    }

    public boolean atualizar(PontoRisco p) {
        return jdbc.update("UPDATE RS_PONTO_RISCO SET ID_BAIRRO = ?, DS_LOCAL = ?, NR_LATITUDE = ?, NR_LONGITUDE = ?,"
                        + " DS_NIVEL_RISCO = ?, NR_LAMINA_MAX_CM = ? WHERE ID_PONTO_RISCO = ?",
                p.getBairroId(), p.getLocal(), p.getLatitude(), p.getLongitude(), p.getNivelRisco().name(),
                p.getLaminaMaximaCm(), p.getId()) > 0;
    }

    public void atualizarLaminaMaxima(Long id, Double laminaCm) {
        jdbc.update("UPDATE RS_PONTO_RISCO SET NR_LAMINA_MAX_CM = ? WHERE ID_PONTO_RISCO = ?", laminaCm, id);
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_PONTO_RISCO WHERE ID_PONTO_RISCO = ?", id) > 0;
    }
}
