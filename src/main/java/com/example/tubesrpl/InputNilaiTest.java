package com.example.tubesrpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.tubesrpl.dto.NilaiInputRequest;
import com.example.tubesrpl.model.Nilai;
import com.example.tubesrpl.repository.NilaiRepository;
import com.example.tubesrpl.service.NilaiService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InputNilaiTest {

    @Mock
    private NilaiRepository nilaiRepository;

    @InjectMocks
    private NilaiService nilaiService;

    private Nilai nilai;
    private NilaiInputRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // setup test
        nilai = Nilai.builder()
                .idNilai(1)
                .nilaiAngka(85.5f)
                .catatan("Baik")
                .npm("6182201069")
                .nip("199901001")
                .idKomponen(1)
                .idKelompok(1)
                .waktuInput(LocalDateTime.now())
                .build();

        request = new NilaiInputRequest();
        request.setNpm("6182201069");
        request.setIdKomponen(1);
        request.setIdKelompok(1);
        request.setNilaiAngka(85.0f);
        request.setCatatan("Baik");
    }

    @Test
    void testConstructorAndGetters() {
        // test constructor
        Nilai fullNilai = Nilai.builder()
                .idNilai(1)
                .nilaiAngka(85.5f)
                .catatan("Baik")
                .npm("6182201069")
                .nip("199901001")
                .idKomponen(1)
                .idKelompok(1)
                .waktuInput(LocalDateTime.now())
                .build();

        assertNotNull(fullNilai);
        assertEquals(1, fullNilai.getIdNilai());
        assertEquals(85.5f, fullNilai.getNilaiAngka());
        assertEquals("Baik", fullNilai.getCatatan());
        assertEquals("6182201069", fullNilai.getNpm());
        assertEquals("199901001", fullNilai.getNip());
        assertEquals(1, fullNilai.getIdKomponen());
        assertEquals(1, fullNilai.getIdKelompok());
    }

    @Test
    void testSettersAndGetters() {
        Nilai testNilai = new Nilai();

        // set setters
        testNilai.setIdNilai(1);
        testNilai.setNilaiAngka(90.0f);
        testNilai.setCatatan("Sangat Baik");
        testNilai.setNpm("2201002");
        testNilai.setNip("198501002");

        // verif getters
        assertEquals(1, testNilai.getIdNilai());
        assertEquals(90.0f, testNilai.getNilaiAngka());
        assertEquals("Sangat Baik", testNilai.getCatatan());
        assertEquals("2201002", testNilai.getNpm());
        assertEquals("198501002", testNilai.getNip());
    }

    @Test
    void testNullValues() {
        // test null
        nilai.setCatatan(null);
        nilai.setWaktuInput(null);

        assertNull(nilai.getCatatan());
        assertNull(nilai.getWaktuInput());
    }

    @Test
    void testEqualsAndHashCode() {
        Nilai nilai1 = new Nilai();
        nilai1.setIdNilai(1);
        nilai1.setNilaiAngka(85.0f);

        Nilai nilai2 = new Nilai();
        nilai2.setIdNilai(1);
        nilai2.setNilaiAngka(85.0f);

        Nilai nilai3 = new Nilai();
        nilai3.setIdNilai(2);
        nilai3.setNilaiAngka(90.0f);

        assertEquals(nilai1, nilai2); // id sama
        assertNotEquals(nilai1, nilai3); // beda
        assertEquals(nilai1.hashCode(), nilai2.hashCode());
    }

    @Test
    void testInputNilai_NewNilai() {
        // nilai belum ada di database
        when(nilaiRepository.findByNpmAndKomponen(request.getNpm(), request.getIdKomponen()))
                .thenReturn(Optional.empty());
        
        Nilai savedNilai = Nilai.builder()
                .idNilai(1)
                .npm(request.getNpm())
                .nip("199901001")
                .idKomponen(request.getIdKomponen())
                .idKelompok(request.getIdKelompok())
                .nilaiAngka(request.getNilaiAngka())
                .catatan(request.getCatatan())
                .build();
        
        when(nilaiRepository.save(any(Nilai.class))).thenReturn(savedNilai);

        Nilai result = nilaiService.inputNilai(request, "199901001");

        // assert
        assertNotNull(result);
        assertEquals(1, result.getIdNilai());
        assertEquals(85.0f, result.getNilaiAngka());
        assertEquals("6182201069", result.getNpm());
        assertEquals("199901001", result.getNip());
        
        verify(nilaiRepository).findByNpmAndKomponen(request.getNpm(), request.getIdKomponen());
        verify(nilaiRepository).save(any(Nilai.class));
    }

    @Test
    void testInputNilai_UpdateExisting() {
        // nilai sudah ada di database
        Nilai existingNilai = Nilai.builder()
                .idNilai(1)
                .npm("6182201069")
                .nip("199901001")
                .idKomponen(1)
                .idKelompok(1)
                .nilaiAngka(80.0f)
                .catatan("Cukup")
                .build();

        when(nilaiRepository.findByNpmAndKomponen(request.getNpm(), request.getIdKomponen()))
                .thenReturn(Optional.of(existingNilai));
        
        Nilai updatedNilai = Nilai.builder()
                .idNilai(1)
                .npm("6182201069")
                .nip("199901001")
                .idKomponen(1)
                .idKelompok(1)
                .nilaiAngka(90.0f)
                .catatan("Sangat Baik")
                .build();
        
        when(nilaiRepository.save(any(Nilai.class))).thenReturn(updatedNilai);

        // update request dengan nilai baru
        request.setNilaiAngka(90.0f);
        request.setCatatan("Sangat Baik");

        Nilai result = nilaiService.inputNilai(request, "199901001");

        // assert
        assertNotNull(result);
        assertEquals(1, result.getIdNilai());
        assertEquals(90.0f, result.getNilaiAngka());
        assertEquals("Sangat Baik", result.getCatatan());
        
        verify(nilaiRepository).findByNpmAndKomponen(request.getNpm(), request.getIdKomponen());
        verify(nilaiRepository).save(any(Nilai.class));
    }
}