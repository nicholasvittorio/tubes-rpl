package com.example.tubesrpl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tubesrpl.model.Pengguna;
import com.example.tubesrpl.service.PenggunaService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private PenggunaService penggunaService;

    @GetMapping("/")
    public String index(HttpSession session) {
        Pengguna user = (Pengguna) session.getAttribute("user");
        if (user != null) {
            return redirectByRole(user.getPeran());
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginView(HttpSession session) {
        // Jika sudah ada session user, redirect ke dashboard sesuai role
        Pengguna user = (Pengguna) session.getAttribute("user");
        if (user != null) {
            return redirectByRole(user.getPeran());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, 
                       @RequestParam("password") String password, 
                       HttpSession session, 
                       Model model) {
        
        Pengguna user = penggunaService.login(username, password);

        if (user != null) {
            // Login berhasil - simpan user ke session
            session.setAttribute("user", user);
            return redirectByRole(user.getPeran());
        } else {
            // Login gagal
            model.addAttribute("error", "Username atau Password salah");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectByRole(String role) {
        if (role == null) return "redirect:/login";
        
        switch (role.toLowerCase()) {
            case "admin":
                return "redirect:/admin/dashboard";
            case "dosen":
                return "redirect:/dosen/dashboard";
            case "mahasiswa":
                return "redirect:/mahasiswa/dashboard";
            default:
                return "redirect:/login";
        }
    }
}
