package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mahasiswa {
    private String npm;
    private Integer idPengguna;
    private Pengguna pengguna;
    private Nilai nilai;
}
