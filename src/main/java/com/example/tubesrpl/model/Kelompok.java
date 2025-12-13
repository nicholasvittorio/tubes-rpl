package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kelompok {
    private Integer idKelompok;
    private String namaKelompok;
    private Integer maksimalAnggota;
    private Integer idKelas;
    private LocalDateTime createdAt;
    private Kelas kelas;
    private List<Mahasiswa> anggota;
    private Integer jumlahAnggota;
    private List<AnggotaInfo> anggotaList;
    
    public Integer getBatasAnggota() {
        return maksimalAnggota;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnggotaInfo {
        private String npm;
        private String nama;
    }
}
