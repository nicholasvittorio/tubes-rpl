package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Pengguna;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PenggunaRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Pengguna> rowMapper = (rs, rowNum) -> Pengguna.builder()
            .idPengguna(rs.getInt("id_pengguna"))
            .username(rs.getString("username"))
            .password(rs.getString("password"))
            .namaLengkap(rs.getString("nama_lengkap"))
            .peran(rs.getString("peran"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
            .build();

    public List<Pengguna> findAll() {
        return jdbcTemplate.query("SELECT * FROM pengguna ORDER BY id_pengguna", rowMapper);
    }

    public Optional<Pengguna> findById(Integer id) {
        List<Pengguna> results = jdbcTemplate.query(
                "SELECT * FROM pengguna WHERE id_pengguna = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Pengguna> findByUsername(String username) {
        List<Pengguna> results = jdbcTemplate.query(
                "SELECT * FROM pengguna WHERE username = ?", rowMapper, username);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Pengguna> findByPeran(String peran) {
        return jdbcTemplate.query(
                "SELECT * FROM pengguna WHERE peran = ? ORDER BY nama_lengkap", rowMapper, peran);
    }

    public Pengguna save(Pengguna pengguna) {
        if (pengguna.getIdPengguna() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO pengguna (username, password, nama_lengkap, peran) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, pengguna.getUsername());
                ps.setString(2, pengguna.getPassword());
                ps.setString(3, pengguna.getNamaLengkap());
                ps.setString(4, pengguna.getPeran());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("id_pengguna");
            pengguna.setIdPengguna(key.intValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE pengguna SET username = ?, password = ?, nama_lengkap = ?, peran = ?, updated_at = ? WHERE id_pengguna = ?",
                    pengguna.getUsername(), pengguna.getPassword(), pengguna.getNamaLengkap(),
                    pengguna.getPeran(), Timestamp.valueOf(java.time.LocalDateTime.now()), pengguna.getIdPengguna());
        }
        return pengguna;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM pengguna WHERE id_pengguna = ?", id);
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pengguna WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pengguna", Long.class);
        return count != null ? count : 0;
    }

    public long countByPeran(String peran) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pengguna WHERE peran = ?", Long.class, peran);
        return count != null ? count : 0;
    }
}
