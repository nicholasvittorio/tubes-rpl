package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Mahasiswa;
import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.repository.MahasiswaRepository;
import com.example.tubesrpl.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MahasiswaService {

    private final MahasiswaRepository mahasiswaRepository;
    private final PenggunaRepository penggunaRepository;

    public List<Mahasiswa> findAll() {
        List<Mahasiswa> mahasiswaList = mahasiswaRepository.findAll();
        mahasiswaList.forEach(m -> {
            Pengguna p = penggunaRepository.findById(m.getIdPengguna()).orElse(null);
            m.setPengguna(p);
        });
        return mahasiswaList;
    }

    public Mahasiswa findByNpm(String npm) {
        Mahasiswa mahasiswa = mahasiswaRepository.findByNpm(npm)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with NPM: " + npm));
        mahasiswa.setPengguna(penggunaRepository.findById(mahasiswa.getIdPengguna()).orElse(null));
        return mahasiswa;
    }

    public Mahasiswa findByIdPengguna(Integer idPengguna) {
        Mahasiswa mahasiswa = mahasiswaRepository.findByIdPengguna(idPengguna)
                .orElseThrow(() -> new ResourceNotFoundException("Mahasiswa not found with id_pengguna: " + idPengguna));
        mahasiswa.setPengguna(penggunaRepository.findById(mahasiswa.getIdPengguna()).orElse(null));
        return mahasiswa;
    }

    public Mahasiswa save(Mahasiswa mahasiswa) {
        return mahasiswaRepository.save(mahasiswa);
    }

    public void deleteByNpm(String npm) {
        mahasiswaRepository.deleteByNpm(npm);
    }

    public List<Mahasiswa> findByKelasId(Integer idKelas) {
        List<Mahasiswa> mahasiswaList = mahasiswaRepository.findByKelasId(idKelas);
        mahasiswaList.forEach(m -> {
            Pengguna p = penggunaRepository.findById(m.getIdPengguna()).orElse(null);
            m.setPengguna(p);
        });
        return mahasiswaList;
    }

    public List<Mahasiswa> findByKelompokId(Integer idKelompok) {
        List<Mahasiswa> mahasiswaList = mahasiswaRepository.findByKelompokId(idKelompok);
        mahasiswaList.forEach(m -> {
            Pengguna p = penggunaRepository.findById(m.getIdPengguna()).orElse(null);
            m.setPengguna(p);
        });
        return mahasiswaList;
    }
}
