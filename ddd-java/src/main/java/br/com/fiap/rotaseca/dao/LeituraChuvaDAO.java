package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.LeituraChuva;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_LEITURA_CHUVA. */
@Repository
public class LeituraChuvaDAO {

    private static final String SELECT = "SELECT ID_LEITURA, ID_BAIRRO, DT_LEITURA, VL_PRECIPITACAO_MM, VL_UMIDADE,"
            + " DS_FONTE FROM RS_LEITURA_CHUVA";

    private static final RowMapper<LeituraChuva> MAPPER = (rs, i) -> new LeituraChuva(
            rs.getLong("ID_LEITURA"),
            rs.getLong("ID_BAIRRO"),
            JdbcUtils.getDataHora(rs, "DT_LEITURA"),
            rs.getDouble("VL_PRECIPITACAO_MM"),
            JdbcUtils.getDoubleOuNulo(rs, "VL_UMIDADE"),
            rs.getString("DS_FONTE"));

    private final JdbcTemplate jdbc;

    public LeituraChuvaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<LeituraChuva> listar() {
        return jdbc.query(SELECT + " ORDER BY DT_LEITURA DESC", MAPPER);
    }

    public List<LeituraChuva> listarPorBairro(Long bairroId) {
        return jdbc.query(SELECT + " WHERE ID_BAIRRO = ? ORDER BY DT_LEITURA DESC", MAPPER, bairroId);
    }

    public Optional<LeituraChuva> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_LEITURA = ?", MAPPER, id).stream().findFirst();
    }

    public LeituraChuva inserir(LeituraChuva l) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_LEITURA_CHUVA (ID_BAIRRO, DT_LEITURA, VL_PRECIPITACAO_MM, VL_UMIDADE, DS_FONTE)"
                            + " VALUES (?, ?, ?, ?, ?)",
                    new String[]{"ID_LEITURA"});
            ps.setLong(1, l.getBairroId());
            ps.setTimestamp(2, JdbcUtils.timestamp(l.getDataHora()));
            ps.setDouble(3, l.getPrecipitacaoMm());
            ps.setObject(4, l.getUmidade(), Types.DOUBLE);
            ps.setString(5, l.getFonte());
            return ps;
        }, chave);
        l.setId(chave.getKey().longValue());
        return l;
    }

    public boolean atualizar(LeituraChuva l) {
        return jdbc.update("UPDATE RS_LEITURA_CHUVA SET ID_BAIRRO = ?, DT_LEITURA = ?, VL_PRECIPITACAO_MM = ?,"
                        + " VL_UMIDADE = ?, DS_FONTE = ? WHERE ID_LEITURA = ?",
                l.getBairroId(), JdbcUtils.timestamp(l.getDataHora()), l.getPrecipitacaoMm(), l.getUmidade(),
                l.getFonte(), l.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_LEITURA_CHUVA WHERE ID_LEITURA = ?", id) > 0;
    }
}
