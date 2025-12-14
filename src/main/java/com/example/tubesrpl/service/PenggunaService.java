package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Dosen;
import com.example.tubesrpl.model.Mahasiswa;
import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PenggunaService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DosenService dosenService;
    private final MahasiswaService mahasiswaService;

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
        return penggunaRepository.save(pengguna);
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

    public Map<String, Object> importFromCsv(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<Pengguna> savedUsers = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            // Skip header line
            String header = reader.readLine();
            lineNumber++;
            
            if (header == null) {
                errors.add("File CSV kosong");
                result.put("success", false);
                result.put("errors", errors);
                return result;
            }

            // Process each line
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] fields = line.split(",");
                
                // Validate field count
                if (fields.length < 4) {
                    errors.add("Baris " + lineNumber + ": Format tidak lengkap (minimal 4 kolom)");
                    errorCount++;
                    continue;
                }

                try {
                    String username = fields[0].trim();
                    String password = fields[1].trim();
                    String namaLengkap = fields[2].trim();
                    String peran = fields[3].trim().toLowerCase();
                    String nipOrNpm = fields.length > 4 ? fields[4].trim() : null;

                    // Validate required fields
                    if (username.isEmpty() || password.isEmpty() || namaLengkap.isEmpty() || peran.isEmpty()) {
                        errors.add("Baris " + lineNumber + ": Field tidak boleh kosong");
                        errorCount++;
                        continue;
                    }

                    // Validate peran
                    if (!peran.equals("admin") && !peran.equals("dosen") && !peran.equals("mahasiswa")) {
                        errors.add("Baris " + lineNumber + ": Peran harus 'admin', 'dosen', atau 'mahasiswa'");
                        errorCount++;
                        continue;
                    }

                    // Check if username exists
                    if (penggunaRepository.existsByUsername(username)) {
                        errors.add("Baris " + lineNumber + ": Username '" + username + "' sudah ada");
                        errorCount++;
                        continue;
                    }

                    // Validate NIP/NPM for dosen/mahasiswa
                    if ((peran.equals("dosen") || peran.equals("mahasiswa")) && 
                        (nipOrNpm == null || nipOrNpm.isEmpty())) {
                        errors.add("Baris " + lineNumber + ": NIP/NPM wajib untuk " + peran);
                        errorCount++;
                        continue;
                    }

                    // Create and save user
                    Pengguna pengguna = Pengguna.builder()
                            .username(username)
                            .password(password) // Will be hashed in save method
                            .namaLengkap(namaLengkap)
                            .peran(peran)
                            .build();

                    Pengguna saved = save(pengguna);
                    
                    if (peran.equals("dosen") && nipOrNpm != null) {
                        Dosen dosen = Dosen.builder()
                                .nip(nipOrNpm)
                                .idPengguna(saved.getIdPengguna())
                                .build();
                        dosenService.save(dosen);
                    } else if (peran.equals("mahasiswa") && nipOrNpm != null) {
                        Mahasiswa mahasiswa = Mahasiswa.builder()
                                .npm(nipOrNpm)
                                .idPengguna(saved.getIdPengguna())
                                .build();
                        mahasiswaService.save(mahasiswa);
                    }
                    
                    savedUsers.add(saved);
                    successCount++;

                } catch (Exception e) {
                    errors.add("Baris " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }

            result.put("success", successCount > 0);
            result.put("successCount", successCount);
            result.put("errorCount", errorCount);
            result.put("errors", errors);
            result.put("savedUsers", savedUsers);

        } catch (Exception e) {
            errors.add("Error membaca file: " + e.getMessage());
            result.put("success", false);
            result.put("errors", errors);
        }

        return result;
    }
}
