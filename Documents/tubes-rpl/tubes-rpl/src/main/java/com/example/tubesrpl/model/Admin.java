package com.example.tubesrpl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private Integer idPengguna;
    private Pengguna pengguna;
}
