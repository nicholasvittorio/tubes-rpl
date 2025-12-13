package com.example.tubesrpl.controller.view;

import com.example.tubesrpl.model.*;
import com.example.tubesrpl.repository.KomponenPenilaianRepository;
import com.example.tubesrpl.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dosen")
@RequiredArgsConstructor
public class DosenViewController {

    private final DosenService dosenService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final KelompokService kelompokService;
    private final MataKuliahService mataKuliahService;
    private final MahasiswaService mahasiswaService;
    private final NilaiService nilaiService;
    private final KomponenPenilaianRepository komponenPenilaianRepository;

    private boolean isDosen(HttpSession session) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        return user != null && "dosen".equalsIgnoreCase(user.getPeran());
    }

    private Dosen getDosenFromSession(HttpSession session, Model model) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        if (user == null) return null;
        model.addAttribute("user", user);
        return dosenService.findByIdPengguna(user.getIdPengguna());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByDosenNip(dosen.getNip());
        
        int totalTugas = 0;
        int totalKelompok = 0;
        for (Kelas kelas : kelasList) {
            totalTugas += tugasBesarService.findByKelasId(kelas.getIdKelas()).size();
            totalKelompok += kelompokService.findByKelasId(kelas.getIdKelas()).size();
        }
        
        model.addAttribute("totalKelas", kelasList.size());
        model.addAttribute("totalTugas", totalTugas);
        model.addAttribute("totalKelompok", totalKelompok);
        return "dosen/dashboard";
    }

    // ==================== KELAS ====================
    @GetMapping("/kelas")
    public String kelasList(HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByDosenNip(dosen.getNip());
        
        // Use Set to track unique mata kuliah IDs
        Set<Integer> seenIds = new HashSet<>();
        List<MataKuliah> mataKuliahList = new ArrayList<>();
        
        for (Kelas kelas : kelasList) {
            if (kelas.getMataKuliah() != null && !seenIds.contains(kelas.getMataKuliah().getIdMatkul())) {
                seenIds.add(kelas.getMataKuliah().getIdMatkul());
                mataKuliahList.add(kelas.getMataKuliah());
            }
        }
        
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("mataKuliahList", mataKuliahList);
        return "dosen/kelas";
    }

    @GetMapping("/kelas/matakuliah/{id}")
    public String kelasByMataKuliah(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        MataKuliah mataKuliah = mataKuliahService.findById(id);
        List<Kelas> kelasList = kelasService.findByDosenNip(dosen.getNip()).stream()
                .filter(k -> k.getIdMatkul() != null && k.getIdMatkul().equals(id))
                .collect(Collectors.toList());
        
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("kelasList", kelasList);
        return "dosen/kelas-detail";
    }

    @GetMapping("/kelas/{id}/tugas")
    public String kelasTugas(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        getDosenFromSession(session, model);
        Kelas kelas = kelasService.findById(id);
        List<TugasBesar> tugasList = tugasBesarService.findByKelasId(id);
        
        // Load komponen for each tugas
        for (TugasBesar tugas : tugasList) {
            List<KomponenPenilaian> komponenList = komponenPenilaianRepository.findByTugasId(tugas.getIdTugas());
            tugas.setKomponenList(komponenList);
        }
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("pageTitle", kelas.getNamaKelas());
        model.addAttribute("mataKuliahId", kelas.getIdMatkul());
        return "dosen/kelas-tugas";
    }

    // ==================== TUGAS BESAR ====================
    @PostMapping("/kelas/{id}/tugas/create")
    public String createTugas(@PathVariable Integer id,
                              @RequestParam String namaTugas,
                              @RequestParam(required = false) String deskripsi,
                              @RequestParam(required = false) String tanggalMulai,
                              @RequestParam(required = false) String tanggalSelesai,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            TugasBesar tugas = TugasBesar.builder()
                    .namaTugas(namaTugas)
                    .deskripsi(deskripsi)
                    .tanggalMulai(tanggalMulai != null && !tanggalMulai.isBlank() ? LocalDate.parse(tanggalMulai) : null)
                    .tanggalSelesai(tanggalSelesai != null && !tanggalSelesai.isBlank() ? LocalDate.parse(tanggalSelesai) : null)
                    .idKelas(id)
                    .build();
            tugasBesarService.save(tugas);
            redirectAttributes.addFlashAttribute("success", "Tugas besar berhasil dibuat");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal membuat tugas besar: " + e.getMessage());
        }
        return "redirect:/dosen/kelas/" + id + "/tugas";
    }

    @PostMapping("/tugas/{id}/delete")
    public String deleteTugas(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            TugasBesar tugas = tugasBesarService.findById(id);
            Integer kelasId = tugas.getIdKelas();
            tugasBesarService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Tugas besar berhasil dihapus");
            return "redirect:/dosen/kelas/" + kelasId + "/tugas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus tugas besar: " + e.getMessage());
            return "redirect:/dosen/kelas";
        }
    }

    // ==================== KOMPONEN PENILAIAN / RUBRIK ====================
    @GetMapping("/tugas/{tugasId}/komponen/{komponenId}")
    public String komponenDetail(@PathVariable Integer tugasId, @PathVariable Integer komponenId,
                                 HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        TugasBesar tugas = tugasBesarService.findById(tugasId);
        KomponenPenilaian komponen = komponenPenilaianRepository.findByIdDirect(komponenId);
        Kelas kelas = kelasService.findById(tugas.getIdKelas());
        List<Kelompok> kelompokList = kelompokService.findByKelasIdWithAnggota(kelas.getIdKelas());
        
        model.addAttribute("tugas", tugas);
        model.addAttribute("komponen", komponen);
        model.addAttribute("kelas", kelas);
        model.addAttribute("kelompokList", kelompokList);
        return "dosen/tugas-detail";
    }

    @PostMapping("/tugas/{tugasId}/komponen/create")
    public String createKomponen(@PathVariable Integer tugasId,
                                 @RequestParam String namaKomponen,
                                 @RequestParam Float bobot,
                                 @RequestParam(required = false) String deskripsiRubrik,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            TugasBesar tugas = tugasBesarService.findById(tugasId);
            KomponenPenilaian komponen = KomponenPenilaian.builder()
                    .namaKomponen(namaKomponen)
                    .bobot(bobot)
                    .deskripsiRubrik(deskripsiRubrik)
                    .idTugas(tugasId)
                    .build();
            komponenPenilaianRepository.save(komponen);
            redirectAttributes.addFlashAttribute("success", "Komponen penilaian berhasil dibuat");
            return "redirect:/dosen/kelas/" + tugas.getIdKelas() + "/tugas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal membuat komponen: " + e.getMessage());
            return "redirect:/dosen/kelas";
        }
    }

    @PostMapping("/komponen/{id}/update")
    public String updateKomponen(@PathVariable Integer id,
                                 @RequestParam String namaKomponen,
                                 @RequestParam Float bobot,
                                 @RequestParam(required = false) String deskripsiRubrik,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            KomponenPenilaian komponen = komponenPenilaianRepository.findByIdDirect(id);
            komponen.setNamaKomponen(namaKomponen);
            komponen.setBobot(bobot);
            komponen.setDeskripsiRubrik(deskripsiRubrik);
            komponenPenilaianRepository.save(komponen);
            
            TugasBesar tugas = tugasBesarService.findById(komponen.getIdTugas());
            redirectAttributes.addFlashAttribute("success", "Komponen berhasil diupdate");
            return "redirect:/dosen/kelas/" + tugas.getIdKelas() + "/tugas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal update komponen: " + e.getMessage());
            return "redirect:/dosen/kelas";
        }
    }

    @PostMapping("/komponen/{id}/delete")
    public String deleteKomponen(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            KomponenPenilaian komponen = komponenPenilaianRepository.findByIdDirect(id);
            TugasBesar tugas = tugasBesarService.findById(komponen.getIdTugas());
            komponenPenilaianRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Komponen berhasil dihapus");
            return "redirect:/dosen/kelas/" + tugas.getIdKelas() + "/tugas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus komponen: " + e.getMessage());
            return "redirect:/dosen/kelas";
        }
    }

    // ==================== KELOMPOK ====================
    @GetMapping("/kelas/{id}/kelompok")
    public String kelasKelompok(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        getDosenFromSession(session, model);
        Kelas kelas = kelasService.findById(id);
        List<Kelompok> kelompokList = kelompokService.findByKelasIdWithAnggota(id);
        List<Mahasiswa> mahasiswaList = mahasiswaService.findByKelasId(id);
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("kelompokList", kelompokList);
        model.addAttribute("mahasiswaList", mahasiswaList);
        return "dosen/kelompok";
    }

    @PostMapping("/kelas/{id}/kelompok/create")
    public String createKelompok(@PathVariable Integer id,
                                 @RequestParam String namaKelompok,
                                 @RequestParam(defaultValue = "5") Integer maksimalAnggota,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            Kelompok kelompok = Kelompok.builder()
                    .namaKelompok(namaKelompok)
                    .maksimalAnggota(maksimalAnggota)
                    .idKelas(id)
                    .build();
            kelompokService.save(kelompok);
            redirectAttributes.addFlashAttribute("success", "Kelompok berhasil dibuat");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal membuat kelompok: " + e.getMessage());
        }
        return "redirect:/dosen/kelas/" + id + "/kelompok";
    }

    @PostMapping("/kelompok/{id}/delete")
    public String deleteKelompok(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            Kelompok kelompok = kelompokService.findById(id);
            Integer kelasId = kelompok.getIdKelas();
            kelompokService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Kelompok berhasil dihapus");
            return "redirect:/dosen/kelas/" + kelasId + "/kelompok";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus kelompok: " + e.getMessage());
            return "redirect:/dosen/kelas";
        }
    }

    @PostMapping("/kelompok/{id}/addMember")
    public String addMemberToKelompok(@PathVariable Integer id,
                                      @RequestParam String npm,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            kelompokService.joinKelompok(id, npm);
            redirectAttributes.addFlashAttribute("success", "Anggota berhasil ditambahkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan anggota: " + e.getMessage());
        }
        Kelompok kelompok = kelompokService.findById(id);
        return "redirect:/dosen/kelas/" + kelompok.getIdKelas() + "/kelompok";
    }

    @PostMapping("/kelompok/{id}/removeMember")
    public String removeMemberFromKelompok(@PathVariable Integer id,
                                           @RequestParam String npm,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            kelompokService.leaveKelompok(id, npm);
            redirectAttributes.addFlashAttribute("success", "Anggota berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus anggota: " + e.getMessage());
        }
        Kelompok kelompok = kelompokService.findById(id);
        return "redirect:/dosen/kelas/" + kelompok.getIdKelas() + "/kelompok";
    }

    // ==================== NILAI ====================
    @GetMapping("/nilai")
    public String nilaiList(HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByDosenNip(dosen.getNip());
        
        // Use Set to track unique mata kuliah IDs
        Set<Integer> seenIds = new HashSet<>();
        List<MataKuliah> mataKuliahList = new ArrayList<>();
        
        for (Kelas kelas : kelasList) {
            if (kelas.getMataKuliah() != null && !seenIds.contains(kelas.getMataKuliah().getIdMatkul())) {
                seenIds.add(kelas.getMataKuliah().getIdMatkul());
                mataKuliahList.add(kelas.getMataKuliah());
            }
        }
        
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("mataKuliahList", mataKuliahList);
        return "dosen/nilai";
    }

    @GetMapping("/nilai/matakuliah/{id}")
    public String nilaiByMataKuliah(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        MataKuliah mataKuliah = mataKuliahService.findById(id);
        List<Kelas> kelasList = kelasService.findByDosenNip(dosen.getNip()).stream()
                .filter(k -> k.getIdMatkul() != null && k.getIdMatkul().equals(id))
                .collect(Collectors.toList());
        
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("kelasList", kelasList);
        return "dosen/nilai-kelas";
    }

    @GetMapping("/nilai/kelas/{id}/tugas")
    public String nilaiKelas(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        getDosenFromSession(session, model);
        Kelas kelas = kelasService.findById(id);
        List<TugasBesar> tugasList = tugasBesarService.findByKelasId(id);
        
        for (TugasBesar tugas : tugasList) {
            List<KomponenPenilaian> komponenList = komponenPenilaianRepository.findByTugasId(tugas.getIdTugas());
            tugas.setKomponenList(komponenList);
        }
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("pageTitle", "Nilai - " + kelas.getNamaKelas());
        model.addAttribute("mataKuliahId", kelas.getIdMatkul());
        return "dosen/nilai-tugas";
    }

    @GetMapping("/nilai/komponen/{id}")
    public String nilaiKomponen(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        Dosen dosen = getDosenFromSession(session, model);
        if (dosen == null) return "redirect:/login";
        
        KomponenPenilaian komponen = komponenPenilaianRepository.findByIdDirect(id);
        TugasBesar tugas = tugasBesarService.findById(komponen.getIdTugas());
        Kelas kelas = kelasService.findById(tugas.getIdKelas());
        List<Kelompok> kelompokList = kelompokService.findByKelasIdWithAnggota(kelas.getIdKelas());
        
        // Get existing nilai for each mahasiswa
        for (Kelompok kelompok : kelompokList) {
            for (Mahasiswa m : kelompok.getAnggota()) {
                Nilai nilai = nilaiService.findByMahasiswaNpmAndKomponenId(m.getNpm(), id);
                m.setNilai(nilai);
            }
        }
        
        model.addAttribute("komponen", komponen);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kelas", kelas);
        model.addAttribute("kelompokList", kelompokList);
        return "dosen/nilai-kelompok";
    }

    @PostMapping("/nilai/komponen/{komponenId}/input")
    public String inputNilai(@PathVariable Integer komponenId,
                             @RequestParam String npm,
                             @RequestParam Float nilaiAngka,
                             @RequestParam(required = false) String catatan,
                             @RequestParam Integer idKelompok,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            Pengguna user = (Pengguna) session.getAttribute("user");
            Dosen dosen = dosenService.findByIdPengguna(user.getIdPengguna());
            
            Nilai existing = nilaiService.findByMahasiswaNpmAndKomponenId(npm, komponenId);
            
            if (existing != null) {
                existing.setNilaiAngka(nilaiAngka);
                existing.setCatatan(catatan);
                nilaiService.save(existing);
            } else {
                Nilai nilai = Nilai.builder()
                        .npm(npm)
                        .nip(dosen.getNip())
                        .idKomponen(komponenId)
                        .idKelompok(idKelompok)
                        .nilaiAngka(nilaiAngka)
                        .catatan(catatan)
                        .build();
                nilaiService.save(nilai);
            }
            
            redirectAttributes.addFlashAttribute("success", "Nilai berhasil disimpan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan nilai: " + e.getMessage());
        }
        return "redirect:/dosen/nilai/komponen/" + komponenId;
    }

    @PostMapping("/nilai/komponen/{komponenId}/batch")
    public String inputNilaiBatch(@PathVariable Integer komponenId,
                                  @RequestParam Integer idKelompok,
                                  @RequestParam Float nilaiAngka,
                                  @RequestParam(required = false) String catatan,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isDosen(session)) {
            return "redirect:/login";
        }
        
        try {
            Pengguna user = (Pengguna) session.getAttribute("user");
            Dosen dosen = dosenService.findByIdPengguna(user.getIdPengguna());
            
            // Get all members of the kelompok
            Kelompok kelompok = kelompokService.findById(idKelompok);
            List<Mahasiswa> anggotaList = kelompokService.findAnggotaByKelompokId(idKelompok);
            
            int savedCount = 0;
            for (Mahasiswa anggota : anggotaList) {
                Nilai existing = nilaiService.findByMahasiswaNpmAndKomponenId(anggota.getNpm(), komponenId);
                
                if (existing != null) {
                    existing.setNilaiAngka(nilaiAngka);
                    existing.setCatatan(catatan);
                    nilaiService.save(existing);
                } else {
                    Nilai nilai = Nilai.builder()
                            .npm(anggota.getNpm())
                            .nip(dosen.getNip())
                            .idKomponen(komponenId)
                            .idKelompok(idKelompok)
                            .nilaiAngka(nilaiAngka)
                            .catatan(catatan)
                            .build();
                    nilaiService.save(nilai);
                }
                savedCount++;
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Nilai berhasil disimpan untuk " + savedCount + " anggota kelompok " + kelompok.getNamaKelompok());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan nilai: " + e.getMessage());
        }
        return "redirect:/dosen/nilai/komponen/" + komponenId;
    }
}
