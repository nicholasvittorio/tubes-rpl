package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.MataKuliah;
import com.example.tubesrpl.repository.MataKuliahRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MataKuliahService {

    private final MataKuliahRepository mataKuliahRepository;

    public List<MataKuliah> findAll() {
        return mataKuliahRepository.findAll();
    }

    public MataKuliah findById(Integer id) {
        return mataKuliahRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mata Kuliah not found with id: " + id));
    }

    public MataKuliah save(MataKuliah mataKuliah) {
        return mataKuliahRepository.save(mataKuliah);
    }

    public void deleteById(Integer id) {
        if (mataKuliahRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Mata Kuliah not found with id: " + id);
        }
        mataKuliahRepository.deleteById(id);
    }
}
