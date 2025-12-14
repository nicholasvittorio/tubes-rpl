package com.example.tubesrpl.repository;

import com.example.tubesrpl.model.Dosen;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DosenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Dosen> rowMapper = (rs, rowNum) -> Dosen.builder()
            .nip(rs.getString("nip"))
            .idPengguna(rs.getInt("id_pengguna"))
            .build();

    public List<Dosen> findAll() {
        return jdbcTemplate.query("SELECT * FROM dosen", rowMapper);
    }

    public Optional<Dosen> findByNip(String nip) {
        List<Dosen> results = jdbcTemplate.query(
                "SELECT * FROM dosen WHERE nip = ?", rowMapper, nip);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Dosen> findByIdPengguna(Integer idPengguna) {
        List<Dosen> results = jdbcTemplate.query(
                "SELECT * FROM dosen WHERE id_pengguna = ?", rowMapper, idPengguna);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Dosen save(Dosen dosen) {
        jdbcTemplate.update("INSERT INTO dosen (nip, id_pengguna) VALUES (?, ?)",
                dosen.getNip(), dosen.getIdPengguna());
        return dosen;
    }

    public void deleteByNip(String nip) {
        jdbcTemplate.update("DELETE FROM dosen WHERE nip = ?", nip);
    }

    public List<String> findNipsByKelasId(Integer idKelas) {
        return jdbcTemplate.queryForList(
                "SELECT nip FROM kelas_dosen WHERE id_kelas = ?", String.class, idKelas);
    }
}
