package com.pji.triagem.services;

import com.pji.triagem.dtos.CriancaResumoResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JdbcCriancaLookupService implements CriancaLookupService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCriancaLookupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CriancaResumoResponse buscarResumo(Long criancaId) {
        return jdbcTemplate.query(
                "SELECT id, nome, avatar_emoji FROM crianca WHERE id = ?",
                ps -> ps.setLong(1, criancaId),
                rs -> {
                    if (!rs.next()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Criança não encontrada");
                    }
                    return mapCrianca(rs);
                }
        );
    }

    private CriancaResumoResponse mapCrianca(ResultSet rs) throws SQLException {
        return new CriancaResumoResponse(rs.getLong("id"), rs.getString("nome"), rs.getString("avatar_emoji"));
    }
}
