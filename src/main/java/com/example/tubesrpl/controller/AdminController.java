package com.example.tubesrpl.controller;

import com.example.tubesrpl.dto.ApiResponse;
import com.example.tubesrpl.model.*;
import com.example.tubesrpl.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PenggunaService penggunaService;
    private final MataKuliahService mataKuliahService;
    private final KelasService kelasService;

    // Pengguna Management
    @GetMapping("/pengguna")
    public ResponseEntity<ApiResponse<List<Pengguna>>> getAllPengguna() {
        return ResponseEntity.ok(ApiResponse.success(penggunaService.findAll()));
    }

    @GetMapping("/pengguna/{id}")
    public ResponseEntity<ApiResponse<Pengguna>> getPenggunaById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(penggunaService.findById(id)));
    }

    @PostMapping("/pengguna")
    public ResponseEntity<ApiResponse<Pengguna>> createPengguna(@Valid @RequestBody Pengguna pengguna) {
        return ResponseEntity.ok(ApiResponse.success("Pengguna created", penggunaService.save(pengguna)));
    }

    @PutMapping("/pengguna/{id}")
    public ResponseEntity<ApiResponse<Pengguna>> updatePengguna(@PathVariable Integer id, @Valid @RequestBody Pengguna pengguna) {
        pengguna.setIdPengguna(id);
        return ResponseEntity.ok(ApiResponse.success("Pengguna updated", penggunaService.save(pengguna)));
    }

    @DeleteMapping("/pengguna/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePengguna(@PathVariable Integer id) {
        penggunaService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Pengguna deleted", null));
    }

    // Mata Kuliah Management
    @GetMapping("/matakuliah")
    public ResponseEntity<ApiResponse<List<MataKuliah>>> getAllMataKuliah() {
        return ResponseEntity.ok(ApiResponse.success(mataKuliahService.findAll()));
    }

    @GetMapping("/matakuliah/{id}")
    public ResponseEntity<ApiResponse<MataKuliah>> getMataKuliahById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(mataKuliahService.findById(id)));
    }

    @PostMapping("/matakuliah")
    public ResponseEntity<ApiResponse<MataKuliah>> createMataKuliah(@Valid @RequestBody MataKuliah mataKuliah) {
        return ResponseEntity.ok(ApiResponse.success("Mata Kuliah created", mataKuliahService.save(mataKuliah)));
    }

    @PutMapping("/matakuliah/{id}")
    public ResponseEntity<ApiResponse<MataKuliah>> updateMataKuliah(@PathVariable Integer id, @Valid @RequestBody MataKuliah mataKuliah) {
        mataKuliah.setIdMatkul(id);
        return ResponseEntity.ok(ApiResponse.success("Mata Kuliah updated", mataKuliahService.save(mataKuliah)));
    }

    @DeleteMapping("/matakuliah/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMataKuliah(@PathVariable Integer id) {
        mataKuliahService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Mata Kuliah deleted", null));
    }

    // Kelas Management
    @GetMapping("/kelas")
    public ResponseEntity<ApiResponse<List<Kelas>>> getAllKelas() {
        return ResponseEntity.ok(ApiResponse.success(kelasService.findAll()));
    }

    @GetMapping("/kelas/{id}")
    public ResponseEntity<ApiResponse<Kelas>> getKelasById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(kelasService.findById(id)));
    }

    @PostMapping("/kelas")
    public ResponseEntity<ApiResponse<Kelas>> createKelas(@Valid @RequestBody Kelas kelas) {
        return ResponseEntity.ok(ApiResponse.success("Kelas created", kelasService.save(kelas)));
    }

    @PutMapping("/kelas/{id}")
    public ResponseEntity<ApiResponse<Kelas>> updateKelas(@PathVariable Integer id, @Valid @RequestBody Kelas kelas) {
        kelas.setIdKelas(id);
        return ResponseEntity.ok(ApiResponse.success("Kelas updated", kelasService.save(kelas)));
    }

    @DeleteMapping("/kelas/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKelas(@PathVariable Integer id) {
        kelasService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Kelas deleted", null));
    }

    @PostMapping("/kelas/{idKelas}/dosen/{nip}")
    public ResponseEntity<ApiResponse<Void>> addDosenToKelas(@PathVariable Integer idKelas, @PathVariable String nip) {
        kelasService.addDosenToKelas(idKelas, nip);
        return ResponseEntity.ok(ApiResponse.success("Dosen added to Kelas", null));
    }
}
