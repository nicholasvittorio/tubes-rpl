package com.example.tubesrpl.controller.view;

import com.example.tubesrpl.model.*;
import com.example.tubesrpl.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final PenggunaService penggunaService;
    private final MataKuliahService mataKuliahService;
    private final KelasService kelasService;
    private final DosenService dosenService;
    private final MahasiswaService mahasiswaService;

    private boolean isAdmin(HttpSession session) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        return user != null && "admin".equalsIgnoreCase(user.getPeran());
    }

    private void setUserToModel(HttpSession session, Model model) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        model.addAttribute("user", user);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        setUserToModel(session, model);
        
        model.addAttribute("totalPengguna", penggunaService.findAll().size());
        model.addAttribute("totalDosen", dosenService.findAll().size());
        model.addAttribute("totalMahasiswa", mahasiswaService.findAll().size());
        model.addAttribute("totalMataKuliah", mataKuliahService.findAll().size());
        model.addAttribute("totalKelas", kelasService.findAll().size());
        
        return "admin/dashboard";
    }

    // ==================== PENGGUNA CRUD ====================
    @GetMapping("/pengguna")
    public String penggunaList(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        setUserToModel(session, model);
        List<Pengguna> penggunaList = penggunaService.findAll();
        model.addAttribute("penggunaList", penggunaList);
        return "admin/pengguna";
    }

    @PostMapping("/pengguna/create")
    public String createPengguna(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String namaLengkap,
                                  @RequestParam String peran,
                                  @RequestParam(required = false) String nip,
                                  @RequestParam(required = false) String npm,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            Pengguna pengguna = Pengguna.builder()
                    .username(username)
                    .password(password)
                    .namaLengkap(namaLengkap)
                    .peran(peran)
                    .build();
            Pengguna savedPengguna = penggunaService.save(pengguna);

            if ("dosen".equals(peran) && nip != null && !nip.isBlank()) {
                Dosen dosen = Dosen.builder()
                        .nip(nip)
                        .idPengguna(savedPengguna.getIdPengguna())
                        .build();
                dosenService.save(dosen);
            } else if ("mahasiswa".equals(peran) && npm != null && !npm.isBlank()) {
                Mahasiswa mahasiswa = Mahasiswa.builder()
                        .npm(npm)
                        .idPengguna(savedPengguna.getIdPengguna())
                        .build();
                mahasiswaService.save(mahasiswa);
            }

            redirectAttributes.addFlashAttribute("success", "Pengguna berhasil ditambahkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan pengguna: " + e.getMessage());
        }
        return "redirect:/admin/pengguna";
    }

    @PostMapping("/pengguna/update/{id}")
    public String updatePengguna(@PathVariable Integer id,
                                  @RequestParam String username,
                                  @RequestParam(required = false) String password,
                                  @RequestParam String namaLengkap,
                                  @RequestParam String peran,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            Pengguna existing = penggunaService.findById(id);
            existing.setUsername(username);
            existing.setNamaLengkap(namaLengkap);
            existing.setPeran(peran);
            if (password != null && !password.isBlank()) {
                existing.setPassword(password);
            }
            penggunaService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Pengguna berhasil diupdate");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupdate pengguna: " + e.getMessage());
        }
        return "redirect:/admin/pengguna";
    }

    @PostMapping("/pengguna/delete/{id}")
    public String deletePengguna(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            penggunaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Pengguna berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus pengguna: " + e.getMessage());
        }
        return "redirect:/admin/pengguna";
    }

    // Endpoint untuk upload CSV
    @PostMapping("/pengguna/import-csv")
    public String importPenggunaFromCsv(@RequestParam("csvFile") MultipartFile file,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            // Validate file
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "File CSV tidak boleh kosong");
                return "redirect:/admin/pengguna";
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                redirectAttributes.addFlashAttribute("error", "File harus berformat CSV");
                return "redirect:/admin/pengguna";
            }

            // Process CSV
            Map<String, Object> result = penggunaService.importFromCsv(file);
            
            if ((boolean) result.get("success")) {
                int successCount = (int) result.get("successCount");
                int errorCount = (int) result.get("errorCount");
                
                // Save dosen/mahasiswa records
                List<Pengguna> savedUsers = (List<Pengguna>) result.get("savedUsers");
                for (Pengguna pengguna : savedUsers) {
                    // This part would need additional CSV column for NIP/NPM
                    // For now, we'll skip creating dosen/mahasiswa records
                    // You can extend this based on your CSV format
                }

                String message = "Berhasil import " + successCount + " pengguna";
                if (errorCount > 0) {
                    List<String> errors = (List<String>) result.get("errors");
                    message += ". " + errorCount + " baris gagal: " + String.join(", ", errors);
                }
                
                redirectAttributes.addFlashAttribute("success", message);
            } else {
                List<String> errors = (List<String>) result.get("errors");
                redirectAttributes.addFlashAttribute("error", "Import gagal: " + String.join(", ", errors));
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal import CSV: " + e.getMessage());
        }

        return "redirect:/admin/pengguna";
    }

    // ==================== MATA KULIAH CRUD ====================
    @GetMapping("/matakuliah")
    public String mataKuliahList(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        setUserToModel(session, model);
        List<MataKuliah> mataKuliahList = mataKuliahService.findAll();
        model.addAttribute("mataKuliahList", mataKuliahList);
        return "admin/matakuliah";
    }

    @PostMapping("/matakuliah/create")
    public String createMataKuliah(@RequestParam String kodeMatkul,
                                    @RequestParam String namaMatkul,
                                    @RequestParam Integer sks,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            MataKuliah mataKuliah = MataKuliah.builder()
                    .kodeMatkul(kodeMatkul)
                    .namaMatkul(namaMatkul)
                    .sks(sks)
                    .build();
            mataKuliahService.save(mataKuliah);
            redirectAttributes.addFlashAttribute("success", "Mata Kuliah berhasil ditambahkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan mata kuliah: " + e.getMessage());
        }
        return "redirect:/admin/matakuliah";
    }

    @PostMapping("/matakuliah/update/{id}")
    public String updateMataKuliah(@PathVariable Integer id,
                                    @RequestParam String kodeMatkul,
                                    @RequestParam String namaMatkul,
                                    @RequestParam Integer sks,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            MataKuliah existing = mataKuliahService.findById(id);
            existing.setKodeMatkul(kodeMatkul);
            existing.setNamaMatkul(namaMatkul);
            existing.setSks(sks);
            mataKuliahService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Mata Kuliah berhasil diupdate");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupdate mata kuliah: " + e.getMessage());
        }
        return "redirect:/admin/matakuliah";
    }

    @PostMapping("/matakuliah/delete/{id}")
    public String deleteMataKuliah(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            mataKuliahService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Mata Kuliah berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus mata kuliah: " + e.getMessage());
        }
        return "redirect:/admin/matakuliah";
    }

    // ==================== KELAS CRUD ====================
    @GetMapping("/kelas")
    public String kelasList(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        setUserToModel(session, model);
        List<Kelas> kelasList = kelasService.findAll();
        List<MataKuliah> mataKuliahList = mataKuliahService.findAll();
        List<Dosen> dosenList = dosenService.findAll();
        List<Mahasiswa> mahasiswaList = mahasiswaService.findAll();
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("mataKuliahList", mataKuliahList);
        model.addAttribute("dosenList", dosenList);
        model.addAttribute("mahasiswaList", mahasiswaList);
        return "admin/kelas";
    }

    @PostMapping("/kelas/create")
    public String createKelas(@RequestParam String namaKelas,
                               @RequestParam Integer idMatkul,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            Kelas kelas = Kelas.builder()
                    .namaKelas(namaKelas)
                    .idMatkul(idMatkul)
                    .build();
            kelasService.save(kelas);
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil ditambahkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan kelas: " + e.getMessage());
        }
        return "redirect:/admin/kelas";
    }

    @PostMapping("/kelas/update/{id}")
    public String updateKelas(@PathVariable Integer id,
                               @RequestParam String namaKelas,
                               @RequestParam Integer idMatkul,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            Kelas existing = kelasService.findById(id);
            existing.setNamaKelas(namaKelas);
            existing.setIdMatkul(idMatkul);
            kelasService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil diupdate");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupdate kelas: " + e.getMessage());
        }
        return "redirect:/admin/kelas";
    }

    @PostMapping("/kelas/delete/{id}")
    public String deleteKelas(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            kelasService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus kelas: " + e.getMessage());
        }
        return "redirect:/admin/kelas";
    }

    @PostMapping("/kelas/{idKelas}/assign-dosen")
    public String assignDosenToKelas(@PathVariable Integer idKelas,
                                      @RequestParam String nip,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            kelasService.addDosenToKelas(idKelas, nip);
            redirectAttributes.addFlashAttribute("success", "Dosen berhasil ditambahkan ke kelas");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan dosen: " + e.getMessage());
        }
        return "redirect:/admin/kelas";
    }

    @PostMapping("/kelas/{idKelas}/assign-mahasiswa")
    public String assignMahasiswaToKelas(@PathVariable Integer idKelas,
                                          @RequestParam String npm,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            kelasService.addMahasiswaToKelas(idKelas, npm);
            redirectAttributes.addFlashAttribute("success", "Mahasiswa berhasil ditambahkan ke kelas");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan mahasiswa: " + e.getMessage());
        }
        return "redirect:/admin/kelas";
    }
}