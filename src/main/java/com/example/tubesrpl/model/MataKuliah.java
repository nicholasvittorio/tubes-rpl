package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MataKuliah {
    private Integer idMatkul;
    private String kodeMatkul;
    private String namaMatkul;
    private Integer sks;
}
