package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Kelompok;
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
public class KelompokRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Kelompok> rowMapper = (rs, rowNum) -> Kelompok.builder()
            .idKelompok(rs.getInt("id_kelompok"))
            .namaKelompok(rs.getString("nama_kelompok"))
            .maksimalAnggota(rs.getInt("maksimal_anggota"))
            .idKelas(rs.getInt("id_kelas"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .build();

    public List<Kelompok> findAll() {
        return jdbcTemplate.query("SELECT * FROM kelompok ORDER BY id_kelompok", rowMapper);
    }

    public Optional<Kelompok> findById(Integer id) {
        List<Kelompok> results = jdbcTemplate.query(
                "SELECT * FROM kelompok WHERE id_kelompok = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Kelompok> findByKelasId(Integer idKelas) {
        return jdbcTemplate.query(
                "SELECT * FROM kelompok WHERE id_kelas = ? ORDER BY id_kelompok", rowMapper, idKelas);
    }

    public List<Kelompok> findByKelasIdWithAnggota(Integer idKelas) {
        String sql = """
            SELECT k.*, 
                   (SELECT COUNT(*) FROM anggota_kelompok ak WHERE ak.id_kelompok = k.id_kelompok) as jumlah_anggota
            FROM kelompok k 
            WHERE k.id_kelas = ? 
            ORDER BY k.nama_kelompok
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> Kelompok.builder()
                .idKelompok(rs.getInt("id_kelompok"))
                .namaKelompok(rs.getString("nama_kelompok"))
                .maksimalAnggota(rs.getInt("maksimal_anggota"))
                .idKelas(rs.getInt("id_kelas"))
                .jumlahAnggota(rs.getInt("jumlah_anggota"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build(), idKelas);
    }

    public Optional<Kelompok> findByMahasiswaNpm(String npm) {
        List<Kelompok> results = jdbcTemplate.query(
                "SELECT k.* FROM kelompok k JOIN anggota_kelompok ak ON k.id_kelompok = ak.id_kelompok WHERE ak.npm = ?",
                rowMapper, npm);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Kelompok save(Kelompok kelompok) {
        if (kelompok.getIdKelompok() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, kelompok.getNamaKelompok());
                ps.setInt(2, kelompok.getMaksimalAnggota() != null ? kelompok.getMaksimalAnggota() : 4);
                ps.setInt(3, kelompok.getIdKelas());
                return ps;
            }, keyHolder);
            kelompok.setIdKelompok(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update(
                    "UPDATE kelompok SET nama_kelompok = ?, maksimal_anggota = ?, id_kelas = ? WHERE id_kelompok = ?",
                    kelompok.getNamaKelompok(), kelompok.getMaksimalAnggota(), kelompok.getIdKelas(), kelompok.getIdKelompok());
        }
        return kelompok;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM kelompok WHERE id_kelompok = ?", id);
    }

    public void addAnggota(Integer idKelompok, String npm) {
        jdbcTemplate.update("INSERT INTO anggota_kelompok (id_kelompok, npm) VALUES (?, ?)", idKelompok, npm);
    }

    public void removeAnggota(Integer idKelompok, String npm) {
        jdbcTemplate.update("DELETE FROM anggota_kelompok WHERE id_kelompok = ? AND npm = ?", idKelompok, npm);
    }

    public int countAnggota(Integer idKelompok) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM anggota_kelompok WHERE id_kelompok = ?", Integer.class, idKelompok);
        return count != null ? count : 0;
    }

    public boolean isMahasiswaInKelompok(Integer idKelompok, String npm) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM anggota_kelompok WHERE id_kelompok = ? AND npm = ?", Integer.class, idKelompok, npm);
        return count != null && count > 0;
    }

    public Optional<Kelompok> findByMahasiswaNpmAndKelasId(String npm, Integer idKelas) {
        List<Kelompok> results = jdbcTemplate.query(
                "SELECT k.* FROM kelompok k " +
                "JOIN anggota_kelompok ak ON k.id_kelompok = ak.id_kelompok " +
                "WHERE ak.npm = ? AND k.id_kelas = ?",
                rowMapper, npm, idKelas);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM kelompok", Long.class);
        return count != null ? count : 0;
    }
}
