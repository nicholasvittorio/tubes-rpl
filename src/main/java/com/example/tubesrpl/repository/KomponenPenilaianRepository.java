package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.KomponenPenilaian;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KomponenPenilaianRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<KomponenPenilaian> rowMapper = (rs, rowNum) -> KomponenPenilaian.builder()
            .idKomponen(rs.getInt("id_komponen"))
            .namaKomponen(rs.getString("nama_komponen"))
            .bobot(rs.getFloat("bobot"))
            .deskripsiRubrik(rs.getString("deskripsi_rubrik"))
            .idTugas(rs.getInt("id_tugas"))
            .build();

    public List<KomponenPenilaian> findAll() {
        return jdbcTemplate.query("SELECT * FROM komponen_penilaian ORDER BY id_komponen", rowMapper);
    }

    public Optional<KomponenPenilaian> findById(Integer id) {
        List<KomponenPenilaian> results = jdbcTemplate.query(
                "SELECT * FROM komponen_penilaian WHERE id_komponen = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public KomponenPenilaian findByIdDirect(Integer id) {
        List<KomponenPenilaian> results = jdbcTemplate.query(
                "SELECT * FROM komponen_penilaian WHERE id_komponen = ?", rowMapper, id);
        if (results.isEmpty()) {
            throw new RuntimeException("Komponen not found with id: " + id);
        }
        return results.get(0);
    }

    public List<KomponenPenilaian> findByTugasId(Integer idTugas) {
        return jdbcTemplate.query(
                "SELECT * FROM komponen_penilaian WHERE id_tugas = ? ORDER BY id_komponen", rowMapper, idTugas);
    }

    public KomponenPenilaian save(KomponenPenilaian komponen) {
        if (komponen.getIdKomponen() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, komponen.getNamaKomponen());
                ps.setFloat(2, komponen.getBobot());
                ps.setString(3, komponen.getDeskripsiRubrik());
                ps.setInt(4, komponen.getIdTugas());
                return ps;
            }, keyHolder);
            komponen.setIdKomponen(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE komponen_penilaian SET nama_komponen = ?, bobot = ?, deskripsi_rubrik = ?, id_tugas = ? WHERE id_komponen = ?",
                    komponen.getNamaKomponen(), komponen.getBobot(), komponen.getDeskripsiRubrik(),
                    komponen.getIdTugas(), komponen.getIdKomponen());
        }
        return komponen;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM komponen_penilaian WHERE id_komponen = ?", id);
    }
}
