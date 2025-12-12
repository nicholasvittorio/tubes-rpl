# Aplikasi Manajemen Tugas Besar — Spring Boot

Aplikasi ini dikembangkan sebagai Tugas Besar mata kuliah Rekayasa Perangkat Lunak di Program Studi Informatika UNPAR.  
Tujuan utama aplikasi adalah mempermudah proses manajemen tugas besar oleh dosen dan mahasiswa, mulai dari pembentukan kelompok, penjadwalan, hingga input nilai per tahap.

---

## Anggota Kelompok

| Nama                                   | NPM        |
|----------------------------------------|------------|
| Nicholas Davin Vittorio                | 6182201027 |
| Christopher                            | 6182201045 |
| Cikal Anugerah Ramadhan                | 6182201069 |
| Azriel Aldrin Senando Siahaan          | 6182201070 |
| Muhammad Gibran Alfarizi               | 6182201099 |

---

## Fitur Utama

### Dosen
- Membuat dan mengelola kelas.
- Membuat komponen penilaian beserta bobotnya.
- Input nilai untuk tiap tahap.
- Mengatur kelompok mahasiswa (assign atau membiarkan mahasiswa membentuk kelompok).
- Memberikan catatan penilaian.
- Melihat rekap nilai mahasiswa.

### Mahasiswa
- Melihat daftar mata kuliah dan tugas besar.
- Membuat atau bergabung ke kelompok.
- Melihat nilai per tahap.
- Melihat catatan yang diberikan oleh dosen.

### Admin
- CRUD pengguna (admin, dosen, mahasiswa).
- CRUD mata kuliah dan kelas.
- Manajemen data sistem.

---

## Teknologi yang Digunakan

- Java 21  
- Spring Boot  
- Spring Security  
- Spring Data JDBC  
- Thymeleaf  
- MySQL  
- Gradle (Kotlin DSL)  

---

src/main/java/com/example/tubesrpl
│── config/
│── controller/
│── dto/
│── exception/
│── model/
│── repository/
│── service/
│── TubesrplApplication.java

src/main/resources
│── templates/
│── static/
│── application.properties
│── schema.sql
│── data.sql

build.gradle.kts
