package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nilai {
    private Integer idNilai;
    private Float nilaiAngka;
    private String catatan;
    private LocalDateTime waktuInput;
    private String npm;
    private String nip;
    private Integer idKomponen;
    private Integer idKelompok;
    
    private Mahasiswa mahasiswa;
    private Dosen dosen;
    private KomponenPenilaian komponenPenilaian;
    private Kelompok kelompok;
    
    private String komponenNama;
    private Float komponenBobot;
    
    private Integer idTugas;
    private String namaTugas;
}
