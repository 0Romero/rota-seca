package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Bairro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_BAIRRO. */
@Repository
public class BairroDAO {

    private static final String SELECT =
            "SELECT ID_BAIRRO, NM_BAIRRO, NM_REGIAO, NM_CIDADE, NR_POPULACAO FROM RS_BAIRRO";

    private static final RowMapper<Bairro> MAPPER = (rs, i) -> new Bairro(
            rs.getLong("ID_BAIRRO"),
            rs.getString("NM_BAIRRO"),
            rs.getString("NM_REGIAO"),
            rs.getString("NM_CIDADE"),
            JdbcUtils.getIntOuNulo(rs, "NR_POPULACAO"));

    private final JdbcTemplate jdbc;

    public BairroDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Bairro> listar() {
        return jdbc.query(SELECT + " ORDER BY NM_BAIRRO", MAPPER);
    }

    public Optional<Bairro> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_BAIRRO = ?", MAPPER, id).stream().findFirst();
    }

    public boolean existe(Long id) {
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM RS_BAIRRO WHERE ID_BAIRRO = ?", Integer.class, id);
        return total != null && total > 0;
    }

    public boolean existeComNome(String nome, String cidade, Long ignorarId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM RS_BAIRRO WHERE UPPER(NM_BAIRRO) = UPPER(?) AND UPPER(NM_CIDADE) = UPPER(?)"
                        + " AND ID_BAIRRO <> ?",
                Integer.class, nome, cidade, ignorarId == null ? -1L : ignorarId);
        return total != null && total > 0;
    }

    public Bairro inserir(Bairro b) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_BAIRRO (NM_BAIRRO, NM_REGIAO, NM_CIDADE, NR_POPULACAO) VALUES (?, ?, ?, ?)",
                    new String[]{"ID_BAIRRO"});
            ps.setString(1, b.getNome());
            ps.setString(2, b.getRegiao());
            ps.setString(3, b.getCidade());
            ps.setObject(4, b.getPopulacao(), Types.INTEGER);
            return ps;
        }, chave);
        b.setId(chave.getKey().longValue());
        return b;
    }

    public boolean atualizar(Bairro b) {
        return jdbc.update(
                "UPDATE RS_BAIRRO SET NM_BAIRRO = ?, NM_REGIAO = ?, NM_CIDADE = ?, NR_POPULACAO = ? WHERE ID_BAIRRO = ?",
                b.getNome(), b.getRegiao(), b.getCidade(), b.getPopulacao(), b.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_BAIRRO WHERE ID_BAIRRO = ?", id) > 0;
    }
}
