package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Mahasiswa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MahasiswaRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mahasiswa> rowMapper = (rs, rowNum) -> Mahasiswa.builder()
            .npm(rs.getString("npm"))
            .idPengguna(rs.getInt("id_pengguna"))
            .build();

    public List<Mahasiswa> findAll() {
        return jdbcTemplate.query("SELECT * FROM mahasiswa", rowMapper);
    }

    public Optional<Mahasiswa> findByNpm(String npm) {
        List<Mahasiswa> results = jdbcTemplate.query(
                "SELECT * FROM mahasiswa WHERE npm = ?", rowMapper, npm);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Mahasiswa> findByIdPengguna(Integer idPengguna) {
        List<Mahasiswa> results = jdbcTemplate.query(
                "SELECT * FROM mahasiswa WHERE id_pengguna = ?", rowMapper, idPengguna);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Mahasiswa save(Mahasiswa mahasiswa) {
        jdbcTemplate.update("INSERT INTO mahasiswa (npm, id_pengguna) VALUES (?, ?)",
                mahasiswa.getNpm(), mahasiswa.getIdPengguna());
        return mahasiswa;
    }

    public void deleteByNpm(String npm) {
        jdbcTemplate.update("DELETE FROM mahasiswa WHERE npm = ?", npm);
    }

    public List<Mahasiswa> findByKelasId(Integer idKelas) {
        return jdbcTemplate.query(
                "SELECT m.* FROM mahasiswa m JOIN kelas_mahasiswa km ON m.npm = km.npm WHERE km.id_kelas = ?",
                rowMapper, idKelas);
    }

    public List<Mahasiswa> findByKelompokId(Integer idKelompok) {
        return jdbcTemplate.query(
                "SELECT m.* FROM mahasiswa m JOIN anggota_kelompok ak ON m.npm = ak.npm WHERE ak.id_kelompok = ?",
                rowMapper, idKelompok);
    }
}
