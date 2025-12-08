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

import java.util.*;

@Controller
@RequestMapping("/mahasiswa")
@RequiredArgsConstructor
public class MahasiswaViewController {

    private final MahasiswaService mahasiswaService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final KelompokService kelompokService;
    private final NilaiService nilaiService;
    private final KomponenPenilaianRepository komponenPenilaianRepository;

    private boolean isMahasiswa(HttpSession session) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        return user != null && "mahasiswa".equalsIgnoreCase(user.getPeran());
    }

    private Mahasiswa getMahasiswaFromSession(HttpSession session, Model model) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        if (user == null) return null;
        model.addAttribute("user", user);
        return mahasiswaService.findByIdPengguna(user.getIdPengguna());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByMahasiswaNpm(mahasiswa.getNpm());
        model.addAttribute("totalMataKuliah", kelasList.size());
        return "mahasiswa/dashboard";
    }

    @PostMapping("/kelas/join")
    public String joinKelas(@RequestParam String kodeKelas, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        try {
            Pengguna user = (Pengguna) session.getAttribute("user");
            Mahasiswa mahasiswa = mahasiswaService.findByIdPengguna(user.getIdPengguna());
            kelasService.joinKelasByKode(kodeKelas, mahasiswa.getNpm());
            redirectAttributes.addFlashAttribute("success", "Berhasil bergabung ke kelas");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mahasiswa/dashboard";
    }

    @GetMapping("/mata-kuliah")
    public String mataKuliahList(HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByMahasiswaNpm(mahasiswa.getNpm());
        model.addAttribute("kelasList", kelasList);
        return "mahasiswa/mata-kuliah";
    }

    @GetMapping("/mata-kuliah/{id}")
    public String mataKuliahDetail(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        Kelas kelas = kelasService.findById(id);
        List<TugasBesar> tugasList = tugasBesarService.findByKelasId(id);
        
        // Load komponen for each tugas
        for (TugasBesar tugas : tugasList) {
            List<KomponenPenilaian> komponenList = komponenPenilaianRepository.findByTugasId(tugas.getIdTugas());
            tugas.setKomponenList(komponenList);
        }
        
        Kelompok myKelompok = kelompokService.findByMahasiswaNpmAndKelasId(mahasiswa.getNpm(), id);
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("myKelompok", myKelompok);
        return "mahasiswa/mata-kuliah-detail";
    }

    @GetMapping("/mata-kuliah/{id}/kelompok")
    public String kelompokList(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        Kelas kelas = kelasService.findById(id);
        List<Kelompok> kelompokList = kelompokService.findByKelasIdWithAnggota(id);
        Kelompok myKelompok = kelompokService.findByMahasiswaNpmAndKelasId(mahasiswa.getNpm(), id);
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("kelompokList", kelompokList);
        model.addAttribute("myKelompok", myKelompok);
        return "mahasiswa/kelompok";
    }

    @PostMapping("/kelompok/{id}/join")
    public String joinKelompok(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        try {
            Pengguna user = (Pengguna) session.getAttribute("user");
            Mahasiswa mahasiswa = mahasiswaService.findByIdPengguna(user.getIdPengguna());
            kelompokService.joinKelompok(id, mahasiswa.getNpm());
            redirectAttributes.addFlashAttribute("success", "Berhasil bergabung ke kelompok");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        Kelompok kelompok = kelompokService.findById(id);
        return "redirect:/mahasiswa/mata-kuliah/" + kelompok.getIdKelas() + "/kelompok";
    }

    @PostMapping("/kelompok/{id}/leave")
    public String leaveKelompok(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        try {
            Pengguna user = (Pengguna) session.getAttribute("user");
            Mahasiswa mahasiswa = mahasiswaService.findByIdPengguna(user.getIdPengguna());
            kelompokService.leaveKelompok(id, mahasiswa.getNpm());
            redirectAttributes.addFlashAttribute("success", "Berhasil keluar dari kelompok");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        Kelompok kelompok = kelompokService.findById(id);
        return "redirect:/mahasiswa/mata-kuliah/" + kelompok.getIdKelas() + "/kelompok";
    }

    @GetMapping("/tugas/{tugasId}/komponen/{komponenId}")
    public String tugasDetail(@PathVariable Integer tugasId, @PathVariable Integer komponenId, 
                              HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        TugasBesar tugas = tugasBesarService.findById(tugasId);
        KomponenPenilaian komponen = komponenPenilaianRepository.findByIdDirect(komponenId);
        Kelas kelas = kelasService.findById(tugas.getIdKelas());
        
        Nilai nilai = nilaiService.findByMahasiswaNpmAndKomponenId(mahasiswa.getNpm(), komponenId);
        
        model.addAttribute("tugas", tugas);
        model.addAttribute("komponen", komponen);
        model.addAttribute("kelas", kelas);
        model.addAttribute("nilai", nilai);
        return "mahasiswa/tugas-detail";
    }

    @GetMapping("/nilai")
    public String nilaiList(HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        List<Kelas> kelasList = kelasService.findByMahasiswaNpm(mahasiswa.getNpm());
        model.addAttribute("kelasList", kelasList);
        return "mahasiswa/nilai";
    }

    @GetMapping("/nilai/kelas/{id}")
    public String nilaiKelasDetail(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isMahasiswa(session)) {
            return "redirect:/login";
        }
        
        Mahasiswa mahasiswa = getMahasiswaFromSession(session, model);
        if (mahasiswa == null) return "redirect:/login";
        
        Kelas kelas = kelasService.findById(id);
        List<Nilai> nilaiList = nilaiService.findByMahasiswaNpmAndKelasId(mahasiswa.getNpm(), id);
        
        Map<Integer, List<Nilai>> nilaiByTugas = new LinkedHashMap<>();
        Map<Integer, String> tugasNames = new LinkedHashMap<>();
        Map<Integer, Float> nilaiAkhirByTugas = new LinkedHashMap<>();
        
        for (Nilai nilai : nilaiList) {
            Integer idTugas = nilai.getIdTugas();
            nilaiByTugas.computeIfAbsent(idTugas, k -> new ArrayList<>()).add(nilai);
            tugasNames.putIfAbsent(idTugas, nilai.getNamaTugas());
        }
        
        // Calculate nilai akhir for each tugas (weighted average)
        for (Map.Entry<Integer, List<Nilai>> entry : nilaiByTugas.entrySet()) {
            float totalWeightedScore = 0;
            float totalWeight = 0;
            for (Nilai n : entry.getValue()) {
                if (n.getNilaiAngka() != null && n.getKomponenBobot() != null) {
                    totalWeightedScore += n.getNilaiAngka() * n.getKomponenBobot() / 100;
                    totalWeight += n.getKomponenBobot();
                }
            }
            float nilaiAkhir = totalWeight > 0 ? (totalWeightedScore / totalWeight) * 100 : 0;
            nilaiAkhirByTugas.put(entry.getKey(), nilaiAkhir);
        }
        
        // Calculate overall final grade
        float overallTotal = 0;
        int tugasCount = 0;
        for (Float nilaiAkhir : nilaiAkhirByTugas.values()) {
            overallTotal += nilaiAkhir;
            tugasCount++;
        }
        float overallNilaiAkhir = tugasCount > 0 ? overallTotal / tugasCount : 0;
        
        model.addAttribute("kelas", kelas);
        model.addAttribute("nilaiByTugas", nilaiByTugas);
        model.addAttribute("tugasNames", tugasNames);
        model.addAttribute("nilaiAkhirByTugas", nilaiAkhirByTugas);
        model.addAttribute("overallNilaiAkhir", overallNilaiAkhir);
        return "mahasiswa/nilai-detail";
    }
}
