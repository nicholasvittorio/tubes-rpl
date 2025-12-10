package com.example.tubesrpl.service;

import com.example.tubesrpl.model.Kelas;
import com.example.tubesrpl.model.MataKuliah;
import com.example.tubesrpl.repository.KelasRepository;
import com.example.tubesrpl.repository.MataKuliahRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminKelasService {

    private final KelasRepository kelasRepository;
    private final MataKuliahRepository mataKuliahRepository;

    public AdminKelasService(KelasRepository kelasRepository,
                             MataKuliahRepository mataKuliahRepository) {
        this.kelasRepository = kelasRepository;
        this.mataKuliahRepository = mataKuliahRepository;
    }

    public List<Kelas> getAllKelas(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return kelasRepository.findAllWithMatkul();
        }
        return kelasRepository.findByKeyword(keyword.trim());
    }

    public List<MataKuliah> getAllMataKuliah() {
        return mataKuliahRepository.findAll();
    }

    public void tambahKelas(String namaKelas, String kodeKelas, Long idMatkul) {
        if (kelasRepository.existsByKodeKelas(kodeKelas)) {
            throw new IllegalArgumentException("Kode kelas '" + kodeKelas + "' sudah digunakan.");
        }
        mataKuliahRepository.findById(idMatkul); // Validasi idMatkul ada
        kelasRepository.save(namaKelas, kodeKelas, idMatkul);
    }

    public void editKelas(Long idKelas, String namaKelas, String kodeKelas, Long idMatkul) {
        // Cek duplikasi kode kelas (kecuali milik diri sendiri)
        if (!kelasRepository.existsByKodeKelasAndIdNot(kodeKelas, idKelas)) {
            throw new IllegalArgumentException("Kode kelas '" + kodeKelas + "' sudah digunakan.");
        }
        mataKuliahRepository.findById(idMatkul); // Validasi idMatkul ada
        kelasRepository.update(idKelas, namaKelas, kodeKelas, idMatkul);
    }

    public void hapusKelas(Long idKelas) {
        kelasRepository.deleteById(idKelas);
    }

    // Metode baru untuk validasi kode kelas unik (kecuali diri sendiri)
    public boolean existsByKodeKelasAndIdNot(String kodeKelas, Long idKelas) {
        String sql = "SELECT 1 FROM kelas WHERE kode_kelas = ? AND id_kelas != ?";
        return !jdbcTemplate.queryForList(sql, Integer.class, kodeKelas, idKelas).isEmpty();
    }
}