package com.example.tubesrpl.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinKelasRequest {
    @NotBlank(message = "Kode kelas is required")
    private String kodeKelas;
}
