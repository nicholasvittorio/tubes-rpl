package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Dosen;
import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.repository.DosenRepository;
import com.example.tubesrpl.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DosenService {

    private final DosenRepository dosenRepository;
    private final PenggunaRepository penggunaRepository;

    public List<Dosen> findAll() {
        List<Dosen> dosenList = dosenRepository.findAll();
        dosenList.forEach(d -> {
            Pengguna p = penggunaRepository.findById(d.getIdPengguna()).orElse(null);
            d.setPengguna(p);
        });
        return dosenList;
    }

    public Dosen findByNip(String nip) {
        Dosen dosen = dosenRepository.findByNip(nip)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen not found with NIP: " + nip));
        dosen.setPengguna(penggunaRepository.findById(dosen.getIdPengguna()).orElse(null));
        return dosen;
    }

    public Dosen findByIdPengguna(Integer idPengguna) {
        Dosen dosen = dosenRepository.findByIdPengguna(idPengguna)
                .orElseThrow(() -> new ResourceNotFoundException("Dosen not found with id_pengguna: " + idPengguna));
        dosen.setPengguna(penggunaRepository.findById(dosen.getIdPengguna()).orElse(null));
        return dosen;
    }

    public Dosen save(Dosen dosen) {
        return dosenRepository.save(dosen);
    }

    public void deleteByNip(String nip) {
        dosenRepository.deleteByNip(nip);
    }
}
