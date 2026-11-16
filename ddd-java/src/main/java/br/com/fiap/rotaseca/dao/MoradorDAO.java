package br.com.fiap.rotaseca.dao;

import br.com.fiap.rotaseca.model.Morador;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela RS_MORADOR. */
@Repository
public class MoradorDAO {

    private static final String SELECT = "SELECT ID_MORADOR, ID_BAIRRO, NM_MORADOR, NR_TELEFONE, DT_NASCIMENTO,"
            + " ST_MOBILIDADE_REDUZIDA, ST_RECEBE_ALERTA FROM RS_MORADOR";

    private static final RowMapper<Morador> MAPPER = (rs, i) -> new Morador(
            rs.getLong("ID_MORADOR"),
            rs.getLong("ID_BAIRRO"),
            rs.getString("NM_MORADOR"),
            rs.getString("NR_TELEFONE"),
            JdbcUtils.getData(rs, "DT_NASCIMENTO"),
            JdbcUtils.booleano(rs, "ST_MOBILIDADE_REDUZIDA"),
            JdbcUtils.booleano(rs, "ST_RECEBE_ALERTA"));

    private final JdbcTemplate jdbc;

    public MoradorDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Morador> listar() {
        return jdbc.query(SELECT + " ORDER BY NM_MORADOR", MAPPER);
    }

    public Optional<Morador> buscarPorId(Long id) {
        return jdbc.query(SELECT + " WHERE ID_MORADOR = ?", MAPPER, id).stream().findFirst();
    }

    public boolean existeTelefone(String telefone, Long ignorarId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM RS_MORADOR WHERE NR_TELEFONE = ? AND ID_MORADOR <> ?",
                Integer.class, telefone, ignorarId == null ? -1L : ignorarId);
        return total != null && total > 0;
    }

    public Morador inserir(Morador m) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO RS_MORADOR (ID_BAIRRO, NM_MORADOR, NR_TELEFONE, DT_NASCIMENTO,"
                            + " ST_MOBILIDADE_REDUZIDA, ST_RECEBE_ALERTA) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_MORADOR"});
            ps.setLong(1, m.getBairroId());
            ps.setString(2, m.getNome());
            ps.setString(3, m.getTelefone());
            ps.setDate(4, JdbcUtils.data(m.getDataNascimento()));
            ps.setString(5, JdbcUtils.simNao(m.getMobilidadeReduzida()));
            ps.setString(6, JdbcUtils.simNao(m.getRecebeAlerta()));
            return ps;
        }, chave);
        m.setId(chave.getKey().longValue());
        return m;
    }

    public boolean atualizar(Morador m) {
        return jdbc.update("UPDATE RS_MORADOR SET ID_BAIRRO = ?, NM_MORADOR = ?, NR_TELEFONE = ?, DT_NASCIMENTO = ?,"
                        + " ST_MOBILIDADE_REDUZIDA = ?, ST_RECEBE_ALERTA = ? WHERE ID_MORADOR = ?",
                m.getBairroId(), m.getNome(), m.getTelefone(), JdbcUtils.data(m.getDataNascimento()),
                JdbcUtils.simNao(m.getMobilidadeReduzida()), JdbcUtils.simNao(m.getRecebeAlerta()), m.getId()) > 0;
    }

    public boolean excluir(Long id) {
        return jdbc.update("DELETE FROM RS_MORADOR WHERE ID_MORADOR = ?", id) > 0;
    }
}
