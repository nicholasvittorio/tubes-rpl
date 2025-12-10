package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Dosen;
import com.example.tubesrpl.model.Mahasiswa;
import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.repository.DosenRepository;
import com.example.tubesrpl.repository.MahasiswaRepository;
import com.example.tubesrpl.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PenggunaService {

    private final PenggunaRepository penggunaRepository;
    private final DosenRepository dosenRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Pengguna> findAll() {
        return penggunaRepository.findAll();
    }

    public Pengguna findById(Integer id) {
        return penggunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna not found with id: " + id));
    }

    public Pengguna findByUsername(String username) {
        return penggunaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna not found with username: " + username));
    }

    public Pengguna login(String username, String password) {
        Optional<Pengguna> userOptional = penggunaRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return null;
        }

        Pengguna user = userOptional.get();

        // Check password matches hash
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }

    public Pengguna save(Pengguna pengguna) {
        // Check username uniqueness only for new users
        if (pengguna.getIdPengguna() == null && penggunaRepository.existsByUsername(pengguna.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        // Encode password if not already encoded
        if (pengguna.getPassword() != null && 
            !pengguna.getPassword().startsWith("$2a$") && 
            !pengguna.getPassword().startsWith("$2y$") &&
            !pengguna.getPassword().startsWith("$2b$")) {
            pengguna.setPassword(passwordEncoder.encode(pengguna.getPassword()));
        }
        Pengguna saved = penggunaRepository.save(pengguna);

        if (saved.getIdPengguna() != null && pengguna.getIdPengguna() == null) {
            if ("dosen".equals(saved.getPeran())) {
                if (pengguna.getNip() == null || pengguna.getNip().isEmpty()) {
                    throw new IllegalArgumentException("NIP wajib diisi untuk dosen");
                }
                Dosen dosen = Dosen.builder()
                        .nip(pengguna.getNip())
                        .idPengguna(saved.getIdPengguna())
                        .build();
                dosenRepository.save(dosen);
            }  else if ("mahasiswa".equals(saved.getPeran())) {
                if (pengguna.getNpm() == null || pengguna.getNpm().isEmpty()) {
                    throw new IllegalArgumentException("NPM wajib diisi untuk mahasiswa");
                }
                Mahasiswa mahasiswa = Mahasiswa.builder()
                        .npm(pengguna.getNpm())
                        .idPengguna(saved.getIdPengguna())
                        .build();
                mahasiswaRepository.save(mahasiswa);
            }
        }
        return saved;
    }

    public void deleteById(Integer id) {
        if (penggunaRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Pengguna not found with id: " + id);
        }
        penggunaRepository.deleteById(id);
    }

    public long count() {
        return penggunaRepository.count();
    }

    public long countByPeran(String peran) {
        return penggunaRepository.countByPeran(peran);
    }
}
