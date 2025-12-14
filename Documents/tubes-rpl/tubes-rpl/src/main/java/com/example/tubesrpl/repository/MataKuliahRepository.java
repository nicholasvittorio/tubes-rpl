package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.MataKuliah;
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
public class MataKuliahRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<MataKuliah> rowMapper = (rs, rowNum) -> MataKuliah.builder()
            .idMatkul(rs.getInt("id_matkul"))
            .kodeMatkul(rs.getString("kode_matkul"))
            .namaMatkul(rs.getString("nama_matkul"))
            .sks(rs.getInt("sks"))
            .build();

    public List<MataKuliah> findAll() {
        return jdbcTemplate.query("SELECT * FROM mata_kuliah ORDER BY id_matkul", rowMapper);
    }

    public Optional<MataKuliah> findById(Integer id) {
        List<MataKuliah> results = jdbcTemplate.query(
                "SELECT * FROM mata_kuliah WHERE id_matkul = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<MataKuliah> findByKode(String kodeMatkul) {
        List<MataKuliah> results = jdbcTemplate.query(
                "SELECT * FROM mata_kuliah WHERE kode_matkul = ?", rowMapper, kodeMatkul);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public MataKuliah save(MataKuliah mataKuliah) {
        if (mataKuliah.getIdMatkul() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO mata_kuliah (kode_matkul, nama_matkul, sks) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, mataKuliah.getKodeMatkul());
                ps.setString(2, mataKuliah.getNamaMatkul());
                ps.setInt(3, mataKuliah.getSks());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("id_matkul");
            mataKuliah.setIdMatkul(key.intValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE mata_kuliah SET kode_matkul = ?, nama_matkul = ?, sks = ? WHERE id_matkul = ?",
                    mataKuliah.getKodeMatkul(), mataKuliah.getNamaMatkul(), mataKuliah.getSks(), mataKuliah.getIdMatkul());
        }
        return mataKuliah;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM mata_kuliah WHERE id_matkul = ?", id);
    }
}
