package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.TugasBesar;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TugasBesarRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TugasBesar> rowMapper = (rs, rowNum) -> TugasBesar.builder()
            .idTugas(rs.getInt("id_tugas"))
            .namaTugas(rs.getString("nama_tugas"))
            .deskripsi(rs.getString("deskripsi"))
            .tanggalMulai(rs.getDate("tanggal_mulai") != null ? rs.getDate("tanggal_mulai").toLocalDate() : null)
            .tanggalSelesai(rs.getDate("tanggal_selesai") != null ? rs.getDate("tanggal_selesai").toLocalDate() : null)
            .idKelas(rs.getInt("id_kelas"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .build();

    public List<TugasBesar> findAll() {
        return jdbcTemplate.query("SELECT * FROM tugas_besar ORDER BY id_tugas", rowMapper);
    }

    public Optional<TugasBesar> findById(Integer id) {
        List<TugasBesar> results = jdbcTemplate.query(
                "SELECT * FROM tugas_besar WHERE id_tugas = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<TugasBesar> findByKelasId(Integer idKelas) {
        return jdbcTemplate.query(
                "SELECT * FROM tugas_besar WHERE id_kelas = ? ORDER BY tanggal_selesai", rowMapper, idKelas);
    }

    public TugasBesar save(TugasBesar tugasBesar) {
        if (tugasBesar.getIdTugas() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas) VALUES (?, ?, ?, ?, ?)",
                        new String[]{"id_tugas"}); // Specify only the ID column
                ps.setString(1, tugasBesar.getNamaTugas());
                ps.setString(2, tugasBesar.getDeskripsi());
                ps.setDate(3, tugasBesar.getTanggalMulai() != null ? Date.valueOf(tugasBesar.getTanggalMulai()) : null);
                ps.setDate(4, tugasBesar.getTanggalSelesai() != null ? Date.valueOf(tugasBesar.getTanggalSelesai()) : null);
                ps.setInt(5, tugasBesar.getIdKelas());
                return ps;
            }, keyHolder);
            
            // Get ID from keys map
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id_tugas")) {
                tugasBesar.setIdTugas(((Number) keys.get("id_tugas")).intValue());
            }
        } else {
            jdbcTemplate.update(
                    "UPDATE tugas_besar SET nama_tugas = ?, deskripsi = ?, tanggal_mulai = ?, tanggal_selesai = ?, id_kelas = ? WHERE id_tugas = ?",
                    tugasBesar.getNamaTugas(),
                    tugasBesar.getDeskripsi(),
                    tugasBesar.getTanggalMulai() != null ? Date.valueOf(tugasBesar.getTanggalMulai()) : null,
                    tugasBesar.getTanggalSelesai() != null ? Date.valueOf(tugasBesar.getTanggalSelesai()) : null,
                    tugasBesar.getIdKelas(), 
                    tugasBesar.getIdTugas());
        }
        return tugasBesar;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM tugas_besar WHERE id_tugas = ?", id);
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tugas_besar", Long.class);
        return count != null ? count : 0;
    }
}
