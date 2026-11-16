package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.PontoApoio;
import br.com.fiap.rotaseca.model.TipoPontoApoio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_PONTO_APOIO. */
@Repository
public class PontoApoioDAO {

    private static final String SELECT = "SELECT ID_PONTO_APOIO, ID_BAIRRO, NM_PONTO, DS_TIPO, DS_ENDERECO,"
            + " NR_LATITUDE, NR_LONGITUDE, NR_CAPACIDADE, ST_ATIVO FROM RS_PONTO_APOIO";

    private static final RowMapper<PontoApoio> MAPPER = (rs, i) -> new PontoApoio(
            rs.getLong("ID_PONTO_APOIO"),
            rs.getLong("ID_BAIRRO"),
            rs.getString("NM_PONTO"),
            TipoPontoApoio.valueOf(rs.getString("DS_TIPO")),
            rs.getString("DS_ENDERECO"),
            rs.getDouble("NR_LATITUDE"),
            rs.getDouble("NR_LONGITUDE"),
            rs.getInt("NR_CAPACIDADE"),
            JdbcUtils.booleano(rs, "ST_ATIVO"));

    private final JdbcTemplate jdbc;

    public PontoApoioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PontoApoio> listar() {
        return jdbc.query(SELECT + " ORDER BY NM_PONTO", MAPPER);
    }

    public List<PontoApoio> listarAtivosPorBairro(Long bairroId) {
        return jdbc.query(SELECT + " WHERE ID_BAIRRO = ? AND ST_ATIVO = 'S' ORDER BY NR_CAPACIDADE DESC",
                MAPPER, bairroId);
    }

    public Optional<PontoApoio> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_PONTO_APOIO = ?", MAPPER, id).stream().findFirst();
    }

    public PontoApoio inserir(PontoApoio p) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_PONTO_APOIO (ID_BAIRRO, NM_PONTO, DS_TIPO, DS_ENDERECO, NR_LATITUDE, NR_LONGITUDE,"
                            + " NR_CAPACIDADE, ST_ATIVO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_PONTO_APOIO"});
            ps.setLong(1, p.getBairroId());
            ps.setString(2, p.getNome());
            ps.setString(3, p.getTipo().name());
            ps.setString(4, p.getEndereco());
            ps.setDouble(5, p.getLatitude());
            ps.setDouble(6, p.getLongitude());
            ps.setInt(7, p.getCapacidade());
            ps.setString(8, JdbcUtils.simNao(p.getAtivo()));
            return ps;
        }, chave);
        p.setId(chave.getKey().longValue());
        return p;
    }

    public boolean atualizar(PontoApoio p) {
        return jdbc.update("UPDATE RS_PONTO_APOIO SET ID_BAIRRO = ?, NM_PONTO = ?, DS_TIPO = ?, DS_ENDERECO = ?,"
                        + " NR_LATITUDE = ?, NR_LONGITUDE = ?, NR_CAPACIDADE = ?, ST_ATIVO = ? WHERE ID_PONTO_APOIO = ?",
                p.getBairroId(), p.getNome(), p.getTipo().name(), p.getEndereco(), p.getLatitude(),
                p.getLongitude(), p.getCapacidade(), JdbcUtils.simNao(p.getAtivo()), p.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_PONTO_APOIO WHERE ID_PONTO_APOIO = ?", id) > 0;
    }
}
