package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TugasBesar {
    private Integer idTugas;
    private String namaTugas;
    private String deskripsi;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private Integer idKelas;
    private LocalDateTime createdAt;
    private Kelas kelas;
    private List<KomponenPenilaian> komponenList;
}
