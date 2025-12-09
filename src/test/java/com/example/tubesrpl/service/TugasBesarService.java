package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.TugasBesar;
import com.example.tubesrpl.repository.TugasBesarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TugasBesarService {

    private final TugasBesarRepository tugasBesarRepository;

    public List<TugasBesar> findAll() {
        return tugasBesarRepository.findAll();
    }

    public TugasBesar findById(Integer id) {
        return tugasBesarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tugas Besar not found with id: " + id));
    }

    public List<TugasBesar> findByKelasId(Integer idKelas) {
        return tugasBesarRepository.findByKelasId(idKelas);
    }

    public TugasBesar save(TugasBesar tugasBesar) {
        return tugasBesarRepository.save(tugasBesar);
    }

    public void deleteById(Integer id) {
        if (tugasBesarRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Tugas Besar not found with id: " + id);
        }
        tugasBesarRepository.deleteById(id);
    }
}
