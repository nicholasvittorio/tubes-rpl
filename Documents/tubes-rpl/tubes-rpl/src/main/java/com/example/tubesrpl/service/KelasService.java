package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Kelas;
import com.example.tubesrpl.model.MataKuliah;
import com.example.tubesrpl.repository.KelasRepository;
import com.example.tubesrpl.repository.MataKuliahRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KelasService {

    private final KelasRepository kelasRepository;
    private final MataKuliahRepository mataKuliahRepository;

    public List<Kelas> findAll() {
        List<Kelas> kelasList = kelasRepository.findAll();
        kelasList.forEach(k -> {
            if (k.getIdMatkul() != null) {
                MataKuliah mk = mataKuliahRepository.findById(k.getIdMatkul()).orElse(null);
                k.setMataKuliah(mk);
            }
        });
        return kelasList;
    }

    public Kelas findById(Integer id) {
        Kelas kelas = kelasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kelas not found with id: " + id));
        if (kelas.getIdMatkul() != null) {
            MataKuliah mk = mataKuliahRepository.findById(kelas.getIdMatkul()).orElse(null);
            kelas.setMataKuliah(mk);
        }
        return kelas;
    }

    public Kelas findByKodeKelas(String kodeKelas) {
        Kelas kelas = kelasRepository.findByKodeKelas(kodeKelas)
                .orElseThrow(() -> new ResourceNotFoundException("Kelas not found with kode: " + kodeKelas));
        if (kelas.getIdMatkul() != null) {
            MataKuliah mk = mataKuliahRepository.findById(kelas.getIdMatkul()).orElse(null);
            kelas.setMataKuliah(mk);
        }
        return kelas;
    }

    public List<Kelas> findByDosenNip(String nip) {
        List<Kelas> kelasList = kelasRepository.findByDosenNip(nip);
        kelasList.forEach(k -> {
            if (k.getIdMatkul() != null) {
                MataKuliah mk = mataKuliahRepository.findById(k.getIdMatkul()).orElse(null);
                k.setMataKuliah(mk);
            }
        });
        return kelasList;
    }

    public List<Kelas> findByMahasiswaNpm(String npm) {
        List<Kelas> kelasList = kelasRepository.findByMahasiswaNpm(npm);
        kelasList.forEach(k -> {
            if (k.getIdMatkul() != null) {
                MataKuliah mk = mataKuliahRepository.findById(k.getIdMatkul()).orElse(null);
                k.setMataKuliah(mk);
            }
        });
        return kelasList;
    }

    public Kelas save(Kelas kelas) {
        if (kelas.getKodeKelas() == null || kelas.getKodeKelas().isBlank()) {
            kelas.setKodeKelas(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return kelasRepository.save(kelas);
    }

    public void deleteById(Integer id) {
        if (kelasRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Kelas not found with id: " + id);
        }
        kelasRepository.deleteById(id);
    }

    public void addDosenToKelas(Integer idKelas, String nip) {
        kelasRepository.addDosenToKelas(idKelas, nip);
    }

    public void addMahasiswaToKelas(Integer idKelas, String npm) {
        if (kelasRepository.isMahasiswaInKelas(idKelas, npm)) {
            throw new IllegalArgumentException("Mahasiswa already in this kelas");
        }
        kelasRepository.addMahasiswaToKelas(idKelas, npm);
    }

    public void joinKelasByKode(String kodeKelas, String npm) {
        Kelas kelas = findByKodeKelas(kodeKelas);
        addMahasiswaToKelas(kelas.getIdKelas(), npm);
    }
}
