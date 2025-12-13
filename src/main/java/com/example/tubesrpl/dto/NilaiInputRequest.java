package com.example.tubesrpl.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NilaiInputRequest {
    @NotNull(message = "NPM is required")
    private String npm;
    
    @NotNull(message = "ID Komponen is required")
    private Integer idKomponen;
    
    @NotNull(message = "ID Kelompok is required")
    private Integer idKelompok;
    
    @NotNull(message = "Nilai is required")
    @Min(value = 0, message = "Nilai minimum is 0")
    @Max(value = 100, message = "Nilai maximum is 100")
    private Float nilaiAngka;
    
    private String catatan;
}
