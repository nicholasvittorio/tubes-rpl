package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Nilai;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NilaiRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Nilai> rowMapper = (rs, rowNum) -> Nilai.builder()
            .idNilai(rs.getInt("id_nilai"))
            .nilaiAngka(rs.getFloat("nilai_angka"))
            .catatan(rs.getString("catatan"))
            .waktuInput(rs.getTimestamp("waktu_input") != null ? rs.getTimestamp("waktu_input").toLocalDateTime() : null)
            .npm(rs.getString("npm"))
            .nip(rs.getString("nip"))
            .idKomponen(rs.getInt("id_komponen"))
            .idKelompok(rs.getInt("id_kelompok"))
            .build();

    public List<Nilai> findAll() {
        return jdbcTemplate.query("SELECT * FROM nilai ORDER BY id_nilai", rowMapper);
    }

    public Optional<Nilai> findById(Integer id) {
        List<Nilai> results = jdbcTemplate.query(
                "SELECT * FROM nilai WHERE id_nilai = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Nilai> findByMahasiswaNpm(String npm) {
        return jdbcTemplate.query(
                "SELECT * FROM nilai WHERE npm = ? ORDER BY id_nilai", rowMapper, npm);
    }

    public List<Nilai> findByKelompokId(Integer idKelompok) {
        return jdbcTemplate.query(
                "SELECT * FROM nilai WHERE id_kelompok = ? ORDER BY id_nilai", rowMapper, idKelompok);
    }

    public List<Nilai> findByKomponenId(Integer idKomponen) {
        return jdbcTemplate.query(
                "SELECT * FROM nilai WHERE id_komponen = ? ORDER BY id_nilai", rowMapper, idKomponen);
    }

    public Optional<Nilai> findByNpmAndKomponen(String npm, Integer idKomponen) {
        List<Nilai> results = jdbcTemplate.query(
                "SELECT * FROM nilai WHERE npm = ? AND id_komponen = ?", rowMapper, npm, idKomponen);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Nilai save(Nilai nilai) {
        if (nilai.getIdNilai() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO nilai (nilai_angka, catatan, npm, nip, id_komponen, id_kelompok) VALUES (?, ?, ?, ?, ?, ?)",
                        new String[]{"id_nilai"});
                ps.setFloat(1, nilai.getNilaiAngka());
                ps.setString(2, nilai.getCatatan() != null ? nilai.getCatatan() : "");
                ps.setString(3, nilai.getNpm());
                ps.setString(4, nilai.getNip());
                ps.setInt(5, nilai.getIdKomponen());
                ps.setInt(6, nilai.getIdKelompok());
                return ps;
            }, keyHolder);
            
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id_nilai")) {
                nilai.setIdNilai(((Number) keys.get("id_nilai")).intValue());
            }
        } else {
            jdbcTemplate.update(
                    "UPDATE nilai SET nilai_angka = ?, catatan = ?, npm = ?, nip = ?, id_komponen = ?, id_kelompok = ? WHERE id_nilai = ?",
                    nilai.getNilaiAngka(), nilai.getCatatan() != null ? nilai.getCatatan() : "", nilai.getNpm(), nilai.getNip(),
                    nilai.getIdKomponen(), nilai.getIdKelompok(), nilai.getIdNilai());
        }
        return nilai;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM nilai WHERE id_nilai = ?", id);
    }

    public List<Nilai> findByMahasiswaNpmAndKelasId(String npm, Integer idKelas) {
        String sql = """
            SELECT n.*, kp.nama_komponen, kp.bobot, tb.id_tugas, tb.nama_tugas 
            FROM nilai n 
            JOIN komponen_penilaian kp ON n.id_komponen = kp.id_komponen 
            JOIN tugas_besar tb ON kp.id_tugas = tb.id_tugas 
            WHERE n.npm = ? AND tb.id_kelas = ? 
            ORDER BY tb.id_tugas, kp.nama_komponen
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Nilai nilai = Nilai.builder()
                    .idNilai(rs.getInt("id_nilai"))
                    .nilaiAngka(rs.getFloat("nilai_angka"))
                    .catatan(rs.getString("catatan"))
                    .waktuInput(rs.getTimestamp("waktu_input") != null ? rs.getTimestamp("waktu_input").toLocalDateTime() : null)
                    .npm(rs.getString("npm"))
                    .nip(rs.getString("nip"))
                    .idKomponen(rs.getInt("id_komponen"))
                    .idKelompok(rs.getInt("id_kelompok"))
                    .komponenNama(rs.getString("nama_komponen"))
                    .komponenBobot(rs.getFloat("bobot"))
                    .idTugas(rs.getInt("id_tugas"))
                    .namaTugas(rs.getString("nama_tugas"))
                    .build();
            return nilai;
        }, npm, idKelas);
    }
}
