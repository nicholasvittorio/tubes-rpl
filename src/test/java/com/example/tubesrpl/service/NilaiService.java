package com.example.tubesrpl.service;

import com.example.tubesrpl.dto.NilaiInputRequest;
import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Nilai;
import com.example.tubesrpl.repository.NilaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NilaiService {

    private final NilaiRepository nilaiRepository;

    public List<Nilai> findAll() {
        return nilaiRepository.findAll();
    }

    public Nilai findById(Integer id) {
        return nilaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nilai not found with id: " + id));
    }

    public List<Nilai> findByMahasiswaNpm(String npm) {
        return nilaiRepository.findByMahasiswaNpm(npm);
    }

    public List<Nilai> findByKelompokId(Integer idKelompok) {
        return nilaiRepository.findByKelompokId(idKelompok);
    }

    public List<Nilai> findByKomponenId(Integer idKomponen) {
        return nilaiRepository.findByKomponenId(idKomponen);
    }

    public Nilai inputNilai(NilaiInputRequest request, String nipDosen) {
        Nilai existing = nilaiRepository.findByNpmAndKomponen(request.getNpm(), request.getIdKomponen())
                .orElse(null);

        Nilai nilai;
        if (existing != null) {
            existing.setNilaiAngka(request.getNilaiAngka());
            existing.setCatatan(request.getCatatan());
            existing.setNip(nipDosen);
            nilai = nilaiRepository.save(existing);
        } else {
            nilai = Nilai.builder()
                    .npm(request.getNpm())
                    .nip(nipDosen)
                    .idKomponen(request.getIdKomponen())
                    .idKelompok(request.getIdKelompok())
                    .nilaiAngka(request.getNilaiAngka())
                    .catatan(request.getCatatan())
                    .build();
            nilai = nilaiRepository.save(nilai);
        }
        return nilai;
    }

    public Nilai save(Nilai nilai) {
        return nilaiRepository.save(nilai);
    }

    public void deleteById(Integer id) {
        if (nilaiRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Nilai not found with id: " + id);
        }
        nilaiRepository.deleteById(id);
    }

    public Nilai findByMahasiswaNpmAndKomponenId(String npm, Integer idKomponen) {
        return nilaiRepository.findByNpmAndKomponen(npm, idKomponen).orElse(null);
    }

    public List<Nilai> findByMahasiswaNpmAndKelasId(String npm, Integer idKelas) {
        return nilaiRepository.findByMahasiswaNpmAndKelasId(npm, idKelas);
    }
}
