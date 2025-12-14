-- =====================================================
-- DUMMY DATA FOR TESTING - FULL SRS SCENARIO
-- Password untuk semua user: "password123"
-- BCrypt encoded: $2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q
-- =====================================================

-- =====================================================
-- 1. INSERT PENGGUNA (Users)
-- =====================================================

-- Admin (password: password123)
INSERT INTO pengguna (username, password, nama_lengkap, peran) VALUES
('admin', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Administrator', 'admin')
ON CONFLICT (username) DO NOTHING;

-- Dosen (password: password123)
INSERT INTO pengguna (username, password, nama_lengkap, peran) VALUES
('dosen1', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dr. Budi Santoso, M.T.', 'dosen'),
('dosen2', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dr. Siti Rahayu, M.Kom.', 'dosen'),
('dosen3', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Prof. Ahmad Wijaya, Ph.D.', 'dosen')
ON CONFLICT (username) DO NOTHING;

-- Mahasiswa (password: password123)
INSERT INTO pengguna (username, password, nama_lengkap, peran) VALUES
('mahasiswa1', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Andi Pratama', 'mahasiswa'),
('mahasiswa2', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dewi Lestari', 'mahasiswa'),
('mahasiswa3', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Rizki Ramadhan', 'mahasiswa'),
('mahasiswa4', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Maya Sari', 'mahasiswa'),
('mahasiswa5', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Budi Hartono', 'mahasiswa'),
('mahasiswa6', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Citra Dewi', 'mahasiswa'),
('mahasiswa7', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dian Permana', 'mahasiswa'),
('mahasiswa8', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Eka Putri', 'mahasiswa')
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- 2. INSERT ADMIN
-- =====================================================
INSERT INTO admin (id_pengguna)
SELECT id_pengguna FROM pengguna WHERE username = 'admin'
ON CONFLICT (id_pengguna) DO NOTHING;

-- =====================================================
-- 3. INSERT DOSEN
-- =====================================================
INSERT INTO dosen (nip, id_pengguna)
SELECT '198501012010011001', id_pengguna FROM pengguna WHERE username = 'dosen1'
ON CONFLICT (nip) DO NOTHING;

INSERT INTO dosen (nip, id_pengguna)
SELECT '198602152011012002', id_pengguna FROM pengguna WHERE username = 'dosen2'
ON CONFLICT (nip) DO NOTHING;

INSERT INTO dosen (nip, id_pengguna)
SELECT '197803202005011003', id_pengguna FROM pengguna WHERE username = 'dosen3'
ON CONFLICT (nip) DO NOTHING;

-- =====================================================
-- 4. INSERT MAHASISWA
-- =====================================================
INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021001', id_pengguna FROM pengguna WHERE username = 'mahasiswa1'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021002', id_pengguna FROM pengguna WHERE username = 'mahasiswa2'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021003', id_pengguna FROM pengguna WHERE username = 'mahasiswa3'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021004', id_pengguna FROM pengguna WHERE username = 'mahasiswa4'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021005', id_pengguna FROM pengguna WHERE username = 'mahasiswa5'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021006', id_pengguna FROM pengguna WHERE username = 'mahasiswa6'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021007', id_pengguna FROM pengguna WHERE username = 'mahasiswa7'
ON CONFLICT (npm) DO NOTHING;

INSERT INTO mahasiswa (npm, id_pengguna)
SELECT '2021008', id_pengguna FROM pengguna WHERE username = 'mahasiswa8'
ON CONFLICT (npm) DO NOTHING;

-- =====================================================
-- 5. INSERT MATA KULIAH
-- =====================================================
INSERT INTO mata_kuliah (kode_matkul, nama_matkul, sks) VALUES
('IF2101', 'Rekayasa Perangkat Lunak', 3),
('IF2102', 'Basis Data', 3),
('IF2103', 'Pemrograman Web', 3),
('IF2104', 'Algoritma dan Struktur Data', 4),
('IF2105', 'Jaringan Komputer', 3)
ON CONFLICT (kode_matkul) DO NOTHING;

-- =====================================================
-- 6. INSERT KELAS
-- =====================================================
INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'RPL Kelas A', 'RPL-A-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2101'
ON CONFLICT (kode_kelas) DO NOTHING;

INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'RPL Kelas B', 'RPL-B-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2101'
ON CONFLICT (kode_kelas) DO NOTHING;

INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'Basis Data Kelas A', 'BD-A-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2102'
ON CONFLICT (kode_kelas) DO NOTHING;

INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'Pemrograman Web Kelas A', 'PW-A-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2103'
ON CONFLICT (kode_kelas) DO NOTHING;

-- =====================================================
-- 7. INSERT KELAS_DOSEN (Dosen mengajar kelas)
-- =====================================================
INSERT INTO kelas_dosen (id_kelas, nip)
SELECT k.id_kelas, '198501012010011001' 
FROM kelas k WHERE k.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelas, nip) DO NOTHING;

INSERT INTO kelas_dosen (id_kelas, nip)
SELECT k.id_kelas, '198501012010011001' 
FROM kelas k WHERE k.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelas, nip) DO NOTHING;

INSERT INTO kelas_dosen (id_kelas, nip)
SELECT k.id_kelas, '198602152011012002' 
FROM kelas k WHERE k.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelas, nip) DO NOTHING;

INSERT INTO kelas_dosen (id_kelas, nip)
SELECT k.id_kelas, '197803202005011003' 
FROM kelas k WHERE k.kode_kelas = 'PW-A-2024'
ON CONFLICT (id_kelas, nip) DO NOTHING;

-- =====================================================
-- 8. INSERT KELAS_MAHASISWA (Mahasiswa mengikuti kelas)
-- =====================================================
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021001' FROM kelas k WHERE k.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021002' FROM kelas k WHERE k.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021003' FROM kelas k WHERE k.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021004' FROM kelas k WHERE k.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021005' FROM kelas k WHERE k.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021006' FROM kelas k WHERE k.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021007' FROM kelas k WHERE k.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021008' FROM kelas k WHERE k.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021001' FROM kelas k WHERE k.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021002' FROM kelas k WHERE k.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021003' FROM kelas k WHERE k.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT k.id_kelas, '2021004' FROM kelas k WHERE k.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelas, npm) DO NOTHING;

-- =====================================================
-- 9. INSERT TUGAS BESAR (with ON CONFLICT for unique constraint)
-- =====================================================
INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas)
SELECT 'Tugas Besar RPL - Aplikasi Manajemen', 
       'Membuat aplikasi manajemen tugas besar berbasis web menggunakan Spring Boot dan Thymeleaf.', 
       '2024-09-01', '2024-12-15', id_kelas
FROM kelas WHERE kode_kelas = 'RPL-A-2024'
ON CONFLICT (nama_tugas, id_kelas) DO NOTHING;

INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas)
SELECT 'Tugas Besar RPL - E-Commerce', 
       'Membuat aplikasi e-commerce sederhana dengan fitur katalog produk dan checkout.', 
       '2024-09-01', '2024-12-15', id_kelas
FROM kelas WHERE kode_kelas = 'RPL-B-2024'
ON CONFLICT (nama_tugas, id_kelas) DO NOTHING;

INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas)
SELECT 'Tugas Besar Basis Data', 
       'Merancang dan mengimplementasikan basis data untuk sistem informasi akademik.', 
       '2024-09-15', '2024-12-20', id_kelas
FROM kelas WHERE kode_kelas = 'BD-A-2024'
ON CONFLICT (nama_tugas, id_kelas) DO NOTHING;

-- =====================================================
-- 10. INSERT KOMPONEN PENILAIAN (with ON CONFLICT)
-- =====================================================
INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Proposal', 15.0, 
       'Penilaian proposal meliputi: latar belakang masalah, tujuan dan manfaat, metodologi pengembangan.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL - Aplikasi Manajemen'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Progress 1 - Analisis & Desain', 20.0, 
       'Penilaian progress 1: Use Case Diagram, Class Diagram, Sequence Diagram, ERD, UI/UX Mockup.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL - Aplikasi Manajemen'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Progress 2 - Implementasi', 20.0, 
       'Penilaian progress 2: Implementasi backend, frontend, Integrasi database, Unit testing.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL - Aplikasi Manajemen'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Demo Final', 30.0, 
       'Penilaian demo final: Kelengkapan fitur, Kualitas UI/UX, Performa aplikasi, Presentasi tim.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL - Aplikasi Manajemen'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Laporan Akhir', 15.0, 
       'Penilaian laporan akhir: Kelengkapan dokumentasi, Kualitas penulisan, Dokumentasi teknis.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL - Aplikasi Manajemen'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Desain Database', 30.0, 
       'Penilaian desain: ERD yang lengkap, Normalisasi hingga 3NF, Relasi antar tabel.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Implementasi SQL', 40.0, 
       'Penilaian implementasi: DDL statements, DML operations, Query kompleks dengan JOIN.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Presentasi Final', 30.0, 
       'Penilaian presentasi: Demo aplikasi, Penjelasan teknis, Q&A session.', 
       id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data'
ON CONFLICT (nama_komponen, id_tugas) DO NOTHING;

-- =====================================================
-- 11. INSERT KELOMPOK (with ON CONFLICT)
-- =====================================================
INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok Alpha', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-A-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok Beta', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-A-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok Gamma', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-B-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok Delta', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-B-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok DB-1', 4, id_kelas FROM kelas WHERE kode_kelas = 'BD-A-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok DB-2', 4, id_kelas FROM kelas WHERE kode_kelas = 'BD-A-2024'
ON CONFLICT (nama_kelompok, id_kelas) DO NOTHING;

-- =====================================================
-- 12. INSERT ANGGOTA KELOMPOK
-- =====================================================
INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021001'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Alpha' AND kl.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021002'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Alpha' AND kl.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021003'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Beta' AND kl.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021004'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Beta' AND kl.kode_kelas = 'RPL-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021005'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Gamma' AND kl.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021006'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Gamma' AND kl.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021007'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Delta' AND kl.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021008'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok Delta' AND kl.kode_kelas = 'RPL-B-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021001'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok DB-1' AND kl.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021002'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok DB-1' AND kl.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021003'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok DB-2' AND kl.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021004'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok DB-2' AND kl.kode_kelas = 'BD-A-2024'
ON CONFLICT (id_kelompok, npm) DO NOTHING;
