package com.example.tubesrpl.service;

import com.example.tubesrpl.exception.ResourceNotFoundException;
import com.example.tubesrpl.model.Kelompok;
import com.example.tubesrpl.model.Mahasiswa;
import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.repository.KelompokRepository;
import com.example.tubesrpl.repository.MahasiswaRepository;
import com.example.tubesrpl.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KelompokService {

    private final KelompokRepository kelompokRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PenggunaRepository penggunaRepository;

    public List<Kelompok> findAll() {
        return kelompokRepository.findAll();
    }

    public Kelompok findById(Integer id) {
        Kelompok kelompok = kelompokRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kelompok not found with id: " + id));
        kelompok.setAnggota(mahasiswaRepository.findByKelompokId(id));
        return kelompok;
    }

    public List<Kelompok> findByKelasId(Integer idKelas) {
        List<Kelompok> kelompokList = kelompokRepository.findByKelasId(idKelas);
        kelompokList.forEach(k -> k.setAnggota(mahasiswaRepository.findByKelompokId(k.getIdKelompok())));
        return kelompokList;
    }

    public List<Kelompok> findByKelasIdWithAnggota(Integer idKelas) {
        List<Kelompok> kelompokList = kelompokRepository.findByKelasId(idKelas);
        for (Kelompok k : kelompokList) {
            List<Mahasiswa> anggota = mahasiswaRepository.findByKelompokId(k.getIdKelompok());
            
            // Load pengguna for each mahasiswa
            for (Mahasiswa m : anggota) {
                if (m.getIdPengguna() != null) {
                    Pengguna pengguna = penggunaRepository.findById(m.getIdPengguna()).orElse(null);
                    m.setPengguna(pengguna);
                }
            }
            
            k.setAnggota(anggota);
            k.setJumlahAnggota(anggota.size());
            
            // Build anggotaList with npm and nama
            List<Kelompok.AnggotaInfo> anggotaInfoList = new ArrayList<>();
            for (Mahasiswa m : anggota) {
                String namaLengkap = m.getPengguna() != null ? m.getPengguna().getNamaLengkap() : "Unknown";
                anggotaInfoList.add(Kelompok.AnggotaInfo.builder()
                        .npm(m.getNpm())
                        .nama(namaLengkap)
                        .build());
            }
            k.setAnggotaList(anggotaInfoList);
        }
        return kelompokList;
    }

    public Kelompok findByMahasiswaNpmAndKelasId(String npm, Integer idKelas) {
        return kelompokRepository.findByMahasiswaNpmAndKelasId(npm, idKelas).orElse(null);
    }

    public Kelompok save(Kelompok kelompok) {
        return kelompokRepository.save(kelompok);
    }

    public void deleteById(Integer id) {
        if (kelompokRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Kelompok not found with id: " + id);
        }
        kelompokRepository.deleteById(id);
    }

    public void joinKelompok(Integer idKelompok, String npm) {
        Kelompok kelompok = findById(idKelompok);
        int currentCount = kelompokRepository.countAnggota(idKelompok);
        if (currentCount >= kelompok.getMaksimalAnggota()) {
            throw new IllegalArgumentException("Kelompok is full");
        }
        if (kelompokRepository.isMahasiswaInKelompok(idKelompok, npm)) {
            throw new IllegalArgumentException("Already a member of this kelompok");
        }
        // Check if already in another kelompok in same kelas
        Kelompok existing = findByMahasiswaNpmAndKelasId(npm, kelompok.getIdKelas());
        if (existing != null) {
            throw new IllegalArgumentException("Already a member of another kelompok in this kelas");
        }
        kelompokRepository.addAnggota(idKelompok, npm);
    }

    public void leaveKelompok(Integer idKelompok, String npm) {
        kelompokRepository.removeAnggota(idKelompok, npm);
    }

    public List<Mahasiswa> getAnggota(Integer idKelompok) {
        return mahasiswaRepository.findByKelompokId(idKelompok);
    }

    public List<Mahasiswa> findAnggotaByKelompokId(Integer idKelompok) {
        List<Mahasiswa> anggota = mahasiswaRepository.findByKelompokId(idKelompok);
        for (Mahasiswa m : anggota) {
            if (m.getIdPengguna() != null) {
                Pengguna pengguna = penggunaRepository.findById(m.getIdPengguna()).orElse(null);
                m.setPengguna(pengguna);
            }
        }
        return anggota;
    }
}
