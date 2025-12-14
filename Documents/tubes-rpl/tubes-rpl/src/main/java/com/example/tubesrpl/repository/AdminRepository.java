package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Admin> rowMapper = (rs, rowNum) -> Admin.builder()
            .idPengguna(rs.getInt("id_pengguna"))
            .build();

    public List<Admin> findAll() {
        return jdbcTemplate.query("SELECT * FROM admin", rowMapper);
    }

    public Optional<Admin> findById(Integer idPengguna) {
        List<Admin> results = jdbcTemplate.query(
                "SELECT * FROM admin WHERE id_pengguna = ?", rowMapper, idPengguna);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Admin save(Admin admin) {
        jdbcTemplate.update("INSERT INTO admin (id_pengguna) VALUES (?)", admin.getIdPengguna());
        return admin;
    }

    public void deleteById(Integer idPengguna) {
        jdbcTemplate.update("DELETE FROM admin WHERE id_pengguna = ?", idPengguna);
    }
}
