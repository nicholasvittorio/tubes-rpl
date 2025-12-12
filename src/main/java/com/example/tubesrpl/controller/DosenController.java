package com.example.tubesrpl.controller;

import com.example.tubesrpl.dto.ApiResponse;
import com.example.tubesrpl.dto.NilaiInputRequest;
import com.example.tubesrpl.model.*;
import com.example.tubesrpl.repository.KomponenPenilaianRepository;
import com.example.tubesrpl.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dosen")
@RequiredArgsConstructor
public class DosenController {

    private final DosenService dosenService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final KelompokService kelompokService;
    private final NilaiService nilaiService;
    private final PenggunaService penggunaService;
    private final KomponenPenilaianRepository komponenPenilaianRepository;

    private String getNipFromAuth(Authentication auth) {
        Pengguna pengguna = penggunaService.findByUsername(auth.getName());
        Dosen dosen = dosenService.findByIdPengguna(pengguna.getIdPengguna());
        return dosen.getNip();
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Dosen>> getProfile(Authentication auth) {
        Pengguna pengguna = penggunaService.findByUsername(auth.getName());
        Dosen dosen = dosenService.findByIdPengguna(pengguna.getIdPengguna());
        return ResponseEntity.ok(ApiResponse.success(dosen));
    }

    @GetMapping("/kelas")
    public ResponseEntity<ApiResponse<List<Kelas>>> getMyKelas(Authentication auth) {
        String nip = getNipFromAuth(auth);
        return ResponseEntity.ok(ApiResponse.success(kelasService.findByDosenNip(nip)));
    }

    @GetMapping("/kelas/{idKelas}/tugas")
    public ResponseEntity<ApiResponse<List<TugasBesar>>> getTugasByKelas(@PathVariable Integer idKelas) {
        return ResponseEntity.ok(ApiResponse.success(tugasBesarService.findByKelasId(idKelas)));
    }

    @PostMapping("/tugas")
    public ResponseEntity<ApiResponse<TugasBesar>> createTugas(@Valid @RequestBody TugasBesar tugas) {
        return ResponseEntity.ok(ApiResponse.success("Tugas created", tugasBesarService.save(tugas)));
    }

    @PutMapping("/tugas/{id}")
    public ResponseEntity<ApiResponse<TugasBesar>> updateTugas(@PathVariable Integer id, @Valid @RequestBody TugasBesar tugas) {
        tugas.setIdTugas(id);
        return ResponseEntity.ok(ApiResponse.success("Tugas updated", tugasBesarService.save(tugas)));
    }

    @DeleteMapping("/tugas/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTugas(@PathVariable Integer id) {
        tugasBesarService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Tugas deleted", null));
    }

    @GetMapping("/tugas/{idTugas}/komponen")
    public ResponseEntity<ApiResponse<List<KomponenPenilaian>>> getKomponenByTugas(@PathVariable Integer idTugas) {
        return ResponseEntity.ok(ApiResponse.success(komponenPenilaianRepository.findByTugasId(idTugas)));
    }

    @PostMapping("/komponen")
    public ResponseEntity<ApiResponse<KomponenPenilaian>> createKomponen(@Valid @RequestBody KomponenPenilaian komponen) {
        return ResponseEntity.ok(ApiResponse.success("Komponen created", komponenPenilaianRepository.save(komponen)));
    }

    @PutMapping("/komponen/{id}")
    public ResponseEntity<ApiResponse<KomponenPenilaian>> updateKomponen(@PathVariable Integer id, @Valid @RequestBody KomponenPenilaian komponen) {
        komponen.setIdKomponen(id);
        return ResponseEntity.ok(ApiResponse.success("Komponen updated", komponenPenilaianRepository.save(komponen)));
    }

    @DeleteMapping("/komponen/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKomponen(@PathVariable Integer id) {
        komponenPenilaianRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Komponen deleted", null));
    }

    @GetMapping("/kelas/{idKelas}/kelompok")
    public ResponseEntity<ApiResponse<List<Kelompok>>> getKelompokByKelas(@PathVariable Integer idKelas) {
        return ResponseEntity.ok(ApiResponse.success(kelompokService.findByKelasId(idKelas)));
    }

    @PostMapping("/kelompok")
    public ResponseEntity<ApiResponse<Kelompok>> createKelompok(@Valid @RequestBody Kelompok kelompok) {
        return ResponseEntity.ok(ApiResponse.success("Kelompok created", kelompokService.save(kelompok)));
    }

    @PutMapping("/kelompok/{id}")
    public ResponseEntity<ApiResponse<Kelompok>> updateKelompok(@PathVariable Integer id, @Valid @RequestBody Kelompok kelompok) {
        kelompok.setIdKelompok(id);
        return ResponseEntity.ok(ApiResponse.success("Kelompok updated", kelompokService.save(kelompok)));
    }

    @DeleteMapping("/kelompok/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKelompok(@PathVariable Integer id) {
        kelompokService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Kelompok deleted", null));
    }

    @PostMapping("/nilai")
    public ResponseEntity<ApiResponse<Nilai>> inputNilai(Authentication auth, @Valid @RequestBody NilaiInputRequest request) {
        String nip = getNipFromAuth(auth);
        Nilai nilai = nilaiService.inputNilai(request, nip);
        return ResponseEntity.ok(ApiResponse.success("Nilai saved", nilai));
    }

    @GetMapping("/kelompok/{idKelompok}/nilai")
    public ResponseEntity<ApiResponse<List<Nilai>>> getNilaiByKelompok(@PathVariable Integer idKelompok) {
        return ResponseEntity.ok(ApiResponse.success(nilaiService.findByKelompokId(idKelompok)));
    }
}
