package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Kelas;
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
public class KelasRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Kelas> rowMapper = (rs, rowNum) -> Kelas.builder()
            .idKelas(rs.getInt("id_kelas"))
            .namaKelas(rs.getString("nama_kelas"))
            .kodeKelas(rs.getString("kode_kelas"))
            .idMatkul(rs.getInt("id_matkul"))
            .build();

    public List<Kelas> findAll() {
        return jdbcTemplate.query("SELECT * FROM kelas ORDER BY id_kelas", rowMapper);
    }

    public Optional<Kelas> findById(Integer id) {
        List<Kelas> results = jdbcTemplate.query(
                "SELECT * FROM kelas WHERE id_kelas = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Kelas> findByKodeKelas(String kodeKelas) {
        List<Kelas> results = jdbcTemplate.query(
                "SELECT * FROM kelas WHERE kode_kelas = ?", rowMapper, kodeKelas);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Kelas save(Kelas kelas) {
        if (kelas.getIdKelas() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, kelas.getNamaKelas());
                ps.setString(2, kelas.getKodeKelas());
                ps.setInt(3, kelas.getIdMatkul());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("id_kelas");
            kelas.setIdKelas(key.intValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE kelas SET nama_kelas = ?, kode_kelas = ?, id_matkul = ? WHERE id_kelas = ?",
                    kelas.getNamaKelas(), kelas.getKodeKelas(), kelas.getIdMatkul(), kelas.getIdKelas());
        }
        return kelas;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM kelas WHERE id_kelas = ?", id);
    }

    public List<Kelas> findByDosenNip(String nip) {
        return jdbcTemplate.query(
                "SELECT k.* FROM kelas k JOIN kelas_dosen kd ON k.id_kelas = kd.id_kelas WHERE kd.nip = ?",
                rowMapper, nip);
    }

    public List<Kelas> findByMahasiswaNpm(String npm) {
        return jdbcTemplate.query(
                "SELECT k.* FROM kelas k JOIN kelas_mahasiswa km ON k.id_kelas = km.id_kelas WHERE km.npm = ?",
                rowMapper, npm);
    }

    public void addDosenToKelas(Integer idKelas, String nip) {
        jdbcTemplate.update("INSERT INTO kelas_dosen (id_kelas, nip) VALUES (?, ?)", idKelas, nip);
    }

    public void addMahasiswaToKelas(Integer idKelas, String npm) {
        jdbcTemplate.update("INSERT INTO kelas_mahasiswa (id_kelas, npm) VALUES (?, ?)", idKelas, npm);
    }

    public void removeDosenFromKelas(Integer idKelas, String nip) {
        jdbcTemplate.update("DELETE FROM kelas_dosen WHERE id_kelas = ? AND nip = ?", idKelas, nip);
    }

    public void removeMahasiswaFromKelas(Integer idKelas, String npm) {
        jdbcTemplate.update("DELETE FROM kelas_mahasiswa WHERE id_kelas = ? AND npm = ?", idKelas, npm);
    }

    public boolean isMahasiswaInKelas(Integer idKelas, String npm) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM kelas_mahasiswa WHERE id_kelas = ? AND npm = ?", Integer.class, idKelas, npm);
        return count != null && count > 0;
    }
}
