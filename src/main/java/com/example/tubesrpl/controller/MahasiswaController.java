package com.example.tubesrpl.controller;

import com.example.tubesrpl.dto.ApiResponse;
import com.example.tubesrpl.dto.JoinKelasRequest;
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
@RequestMapping("/api/mahasiswa")
@RequiredArgsConstructor
public class MahasiswaController {

    private final MahasiswaService mahasiswaService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final KelompokService kelompokService;
    private final NilaiService nilaiService;
    private final PenggunaService penggunaService;
    private final KomponenPenilaianRepository komponenPenilaianRepository;

    private String getNpmFromAuth(Authentication auth) {
        Pengguna pengguna = penggunaService.findByUsername(auth.getName());
        Mahasiswa mahasiswa = mahasiswaService.findByIdPengguna(pengguna.getIdPengguna());
        return mahasiswa.getNpm();
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Mahasiswa>> getProfile(Authentication auth) {
        Pengguna pengguna = penggunaService.findByUsername(auth.getName());
        Mahasiswa mahasiswa = mahasiswaService.findByIdPengguna(pengguna.getIdPengguna());
        return ResponseEntity.ok(ApiResponse.success(mahasiswa));
    }

    @GetMapping("/kelas")
    public ResponseEntity<ApiResponse<List<Kelas>>> getMyKelas(Authentication auth) {
        String npm = getNpmFromAuth(auth);
        return ResponseEntity.ok(ApiResponse.success(kelasService.findByMahasiswaNpm(npm)));
    }

    @PostMapping("/kelas/join")
    public ResponseEntity<ApiResponse<Void>> joinKelas(Authentication auth, @Valid @RequestBody JoinKelasRequest request) {
        String npm = getNpmFromAuth(auth);
        kelasService.joinKelasByKode(request.getKodeKelas(), npm);
        return ResponseEntity.ok(ApiResponse.success("Successfully joined kelas", null));
    }

    @GetMapping("/kelas/{idKelas}/tugas")
    public ResponseEntity<ApiResponse<List<TugasBesar>>> getTugasByKelas(@PathVariable Integer idKelas) {
        return ResponseEntity.ok(ApiResponse.success(tugasBesarService.findByKelasId(idKelas)));
    }

    @GetMapping("/tugas/{idTugas}/komponen")
    public ResponseEntity<ApiResponse<List<KomponenPenilaian>>> getKomponenByTugas(@PathVariable Integer idTugas) {
        return ResponseEntity.ok(ApiResponse.success(komponenPenilaianRepository.findByTugasId(idTugas)));
    }

    @GetMapping("/kelas/{idKelas}/kelompok")
    public ResponseEntity<ApiResponse<List<Kelompok>>> getKelompokByKelas(@PathVariable Integer idKelas) {
        return ResponseEntity.ok(ApiResponse.success(kelompokService.findByKelasId(idKelas)));
    }

    @PostMapping("/kelompok/{idKelompok}/join")
    public ResponseEntity<ApiResponse<Void>> joinKelompok(Authentication auth, @PathVariable Integer idKelompok) {
        String npm = getNpmFromAuth(auth);
        kelompokService.joinKelompok(idKelompok, npm);
        return ResponseEntity.ok(ApiResponse.success("Successfully joined kelompok", null));
    }

    @PostMapping("/kelompok/{idKelompok}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveKelompok(Authentication auth, @PathVariable Integer idKelompok) {
        String npm = getNpmFromAuth(auth);
        kelompokService.leaveKelompok(idKelompok, npm);
        return ResponseEntity.ok(ApiResponse.success("Successfully left kelompok", null));
    }

    @GetMapping("/nilai")
    public ResponseEntity<ApiResponse<List<Nilai>>> getMyNilai(Authentication auth) {
        String npm = getNpmFromAuth(auth);
        return ResponseEntity.ok(ApiResponse.success(nilaiService.findByMahasiswaNpm(npm)));
    }
}
