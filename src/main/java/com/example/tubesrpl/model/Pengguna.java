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
public class Pengguna {
    private Integer idPengguna;
    private String username;
    private String password;
    private String namaLengkap;
    private String peran; // admin, dosen, mahasiswa
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nip;   
    private String npm;  
}
