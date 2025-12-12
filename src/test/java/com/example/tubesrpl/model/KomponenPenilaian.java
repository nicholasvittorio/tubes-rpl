package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KomponenPenilaian {
    private Integer idKomponen;
    private String namaKomponen;
    private Float bobot;
    private String deskripsiRubrik;
    private Integer idTugas;
    private TugasBesar tugasBesar;
}
