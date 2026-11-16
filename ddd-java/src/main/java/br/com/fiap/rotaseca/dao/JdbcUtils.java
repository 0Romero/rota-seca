package br.com.fiap.rotaseca.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Conversões entre tipos Java e tipos do Oracle usadas pelos DAOs. */
final class JdbcUtils {

    private JdbcUtils() {
    }

    static Long getLongOuNulo(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }

    static Integer getIntOuNulo(ResultSet rs, String coluna) throws SQLException {
        int valor = rs.getInt(coluna);
        return rs.wasNull() ? null : valor;
    }

    static Double getDoubleOuNulo(ResultSet rs, String coluna) throws SQLException {
        double valor = rs.getDouble(coluna);
        return rs.wasNull() ? null : valor;
    }

    static LocalDateTime getDataHora(ResultSet rs, String coluna) throws SQLException {
        Timestamp ts = rs.getTimestamp(coluna);
        return ts == null ? null : ts.toLocalDateTime();
    }

    static LocalDate getData(ResultSet rs, String coluna) throws SQLException {
        Date data = rs.getDate(coluna);
        return data == null ? null : data.toLocalDate();
    }

    static Timestamp timestamp(LocalDateTime dataHora) {
        return dataHora == null ? null : Timestamp.valueOf(dataHora);
    }

    static Date data(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }

    /** O Oracle 19c não tem tipo booleano: o modelo usa CHAR(1) com 'S' ou 'N'. */
    static String simNao(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "S" : "N";
    }

    static boolean booleano(ResultSet rs, String coluna) throws SQLException {
        return "S".equals(rs.getString(coluna));
    }
}
