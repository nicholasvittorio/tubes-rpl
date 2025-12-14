package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kelas {
    private Integer idKelas;
    private String namaKelas;
    private String kodeKelas;
    private Integer idMatkul;
    private MataKuliah mataKuliah;
}
