-- =====================================================
-- FRESH START - Drop all and recreate with clean data
-- Run this script to completely reset the database
-- =====================================================

-- Drop all tables in correct order (respect foreign keys)
DROP TABLE IF EXISTS nilai CASCADE;
DROP TABLE IF EXISTS anggota_kelompok CASCADE;
DROP TABLE IF EXISTS kelompok CASCADE;
DROP TABLE IF EXISTS komponen_penilaian CASCADE;
DROP TABLE IF EXISTS tugas_besar CASCADE;
DROP TABLE IF EXISTS kelas_mahasiswa CASCADE;
DROP TABLE IF EXISTS kelas_dosen CASCADE;
DROP TABLE IF EXISTS kelas CASCADE;
DROP TABLE IF EXISTS mata_kuliah CASCADE;
DROP TABLE IF EXISTS mahasiswa CASCADE;
DROP TABLE IF EXISTS dosen CASCADE;
DROP TABLE IF EXISTS admin CASCADE;
DROP TABLE IF EXISTS pengguna CASCADE;

-- =====================================================
-- RECREATE ALL TABLES WITH PROPER CONSTRAINTS
-- =====================================================

-- 1. Tabel Pengguna
CREATE TABLE pengguna (
    id_pengguna SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    peran VARCHAR(20) NOT NULL CHECK (peran IN ('admin', 'dosen', 'mahasiswa')), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabel Admin
CREATE TABLE admin (
    id_pengguna INT PRIMARY KEY,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 3. Tabel Dosen
CREATE TABLE dosen (
    nip VARCHAR(20) PRIMARY KEY, 
    id_pengguna INT NOT NULL UNIQUE,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 4. Tabel Mahasiswa
CREATE TABLE mahasiswa (
    npm VARCHAR(20) PRIMARY KEY, 
    id_pengguna INT NOT NULL UNIQUE, 
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 5. Tabel Mata Kuliah
CREATE TABLE mata_kuliah (
    id_matkul SERIAL PRIMARY KEY,
    kode_matkul VARCHAR(20) NOT NULL UNIQUE,
    nama_matkul VARCHAR(100) NOT NULL,
    sks INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Tabel Kelas 
CREATE TABLE kelas (
    id_kelas SERIAL PRIMARY KEY,
    nama_kelas VARCHAR(50) NOT NULL,
    kode_kelas VARCHAR(20) NOT NULL UNIQUE,
    id_matkul INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE
);

-- 7. Tabel Bridge Kelas-Dosen
CREATE TABLE kelas_dosen (
    id_kelas INT,
    nip VARCHAR(20),
    PRIMARY KEY (id_kelas, nip),
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    FOREIGN KEY (nip) REFERENCES dosen(nip) ON DELETE CASCADE
);

-- 8. Tabel Bridge Kelas-Mahasiswa
CREATE TABLE kelas_mahasiswa (
    id_kelas INT,
    npm VARCHAR(20),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_kelas, npm),
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    FOREIGN KEY (npm) REFERENCES mahasiswa(npm) ON DELETE CASCADE
);

-- 9. Tabel Tugas Besar (with unique constraint to prevent duplicates)
CREATE TABLE tugas_besar (
    id_tugas SERIAL PRIMARY KEY,
    nama_tugas VARCHAR(100) NOT NULL,
    deskripsi TEXT,
    tanggal_mulai DATE,
    tanggal_selesai DATE,
    id_kelas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    UNIQUE(nama_tugas, id_kelas)
);

-- 10. Tabel Komponen Penilaian (with unique constraint)
CREATE TABLE komponen_penilaian (
    id_komponen SERIAL PRIMARY KEY,
    nama_komponen VARCHAR(100) NOT NULL,
    bobot FLOAT NOT NULL,
    deskripsi_rubrik TEXT,
    id_tugas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tugas) REFERENCES tugas_besar(id_tugas) ON DELETE CASCADE,
    UNIQUE(nama_komponen, id_tugas)
);

-- 11. Tabel Kelompok (with unique constraint)
CREATE TABLE kelompok (
    id_kelompok SERIAL PRIMARY KEY,
    nama_kelompok VARCHAR(50) NOT NULL,
    maksimal_anggota INT DEFAULT 4,
    id_kelas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    UNIQUE(nama_kelompok, id_kelas)
);

-- 12. Tabel Anggota Kelompok
CREATE TABLE anggota_kelompok (
    id_kelompok INT,
    npm VARCHAR(20),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_kelompok, npm),
    FOREIGN KEY (id_kelompok) REFERENCES kelompok(id_kelompok) ON DELETE CASCADE,
    FOREIGN KEY (npm) REFERENCES mahasiswa(npm) ON DELETE CASCADE
);

-- 13. Tabel Nilai
CREATE TABLE nilai (
    id_nilai SERIAL PRIMARY KEY,
    nilai_angka FLOAT NOT NULL,
    catatan TEXT,
    waktu_input TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    npm VARCHAR(20) NOT NULL,
    nip VARCHAR(20) NOT NULL,
    id_komponen INT NOT NULL,
    id_kelompok INT NOT NULL,
    FOREIGN KEY (npm) REFERENCES mahasiswa(npm) ON DELETE CASCADE,
    FOREIGN KEY (nip) REFERENCES dosen(nip) ON DELETE RESTRICT,
    FOREIGN KEY (id_komponen) REFERENCES komponen_penilaian(id_komponen) ON DELETE CASCADE,
    FOREIGN KEY (id_kelompok) REFERENCES kelompok(id_kelompok) ON DELETE CASCADE,
    UNIQUE(npm, id_komponen)
);

-- Create indexes
CREATE INDEX idx_pengguna_username ON pengguna(username);
CREATE INDEX idx_pengguna_peran ON pengguna(peran);
CREATE INDEX idx_kelas_kode ON kelas(kode_kelas);
CREATE INDEX idx_nilai_npm ON nilai(npm);
CREATE INDEX idx_nilai_komponen ON nilai(id_komponen);

-- =====================================================
-- INSERT CLEAN DATA
-- Password untuk semua user: "password123"
-- =====================================================

-- 1. Pengguna
INSERT INTO pengguna (username, password, nama_lengkap, peran) VALUES
('admin', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Administrator', 'admin'),
('dosen1', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dr. Budi Santoso, M.T.', 'dosen'),
('dosen2', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dr. Siti Rahayu, M.Kom.', 'dosen'),
('mahasiswa1', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Andi Pratama', 'mahasiswa'),
('mahasiswa2', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Dewi Lestari', 'mahasiswa'),
('mahasiswa3', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Rizki Ramadhan', 'mahasiswa'),
('mahasiswa4', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Maya Sari', 'mahasiswa'),
('mahasiswa5', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Budi Hartono', 'mahasiswa'),
('mahasiswa6', '$2y$10$a7Ic8tzmQl9S.YinmQlnhufUONSwpTo0s.jzt4gTY1FkEShgvW48q', 'Citra Dewi', 'mahasiswa');

-- 2. Admin
INSERT INTO admin (id_pengguna) SELECT id_pengguna FROM pengguna WHERE username = 'admin';

-- 3. Dosen
INSERT INTO dosen (nip, id_pengguna) SELECT '198501012010011001', id_pengguna FROM pengguna WHERE username = 'dosen1';
INSERT INTO dosen (nip, id_pengguna) SELECT '198602152011012002', id_pengguna FROM pengguna WHERE username = 'dosen2';

-- 4. Mahasiswa
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021001', id_pengguna FROM pengguna WHERE username = 'mahasiswa1';
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021002', id_pengguna FROM pengguna WHERE username = 'mahasiswa2';
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021003', id_pengguna FROM pengguna WHERE username = 'mahasiswa3';
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021004', id_pengguna FROM pengguna WHERE username = 'mahasiswa4';
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021005', id_pengguna FROM pengguna WHERE username = 'mahasiswa5';
INSERT INTO mahasiswa (npm, id_pengguna) SELECT '2021006', id_pengguna FROM pengguna WHERE username = 'mahasiswa6';

-- 5. Mata Kuliah
INSERT INTO mata_kuliah (kode_matkul, nama_matkul, sks) VALUES
('IF2101', 'Rekayasa Perangkat Lunak', 3),
('IF2102', 'Basis Data', 3);

-- 6. Kelas
INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'RPL Kelas A', 'RPL-A-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2101';

INSERT INTO kelas (nama_kelas, kode_kelas, id_matkul)
SELECT 'Basis Data Kelas A', 'BD-A-2024', id_matkul FROM mata_kuliah WHERE kode_matkul = 'IF2102';

-- 7. Kelas Dosen
INSERT INTO kelas_dosen (id_kelas, nip)
SELECT id_kelas, '198501012010011001' FROM kelas WHERE kode_kelas = 'RPL-A-2024';

INSERT INTO kelas_dosen (id_kelas, nip)
SELECT id_kelas, '198602152011012002' FROM kelas WHERE kode_kelas = 'BD-A-2024';

-- 8. Kelas Mahasiswa (mahasiswa 1-4 di RPL, 3-6 di BD)
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021001' FROM kelas WHERE kode_kelas = 'RPL-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021002' FROM kelas WHERE kode_kelas = 'RPL-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021003' FROM kelas WHERE kode_kelas = 'RPL-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021004' FROM kelas WHERE kode_kelas = 'RPL-A-2024';

INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021003' FROM kelas WHERE kode_kelas = 'BD-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021004' FROM kelas WHERE kode_kelas = 'BD-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021005' FROM kelas WHERE kode_kelas = 'BD-A-2024';
INSERT INTO kelas_mahasiswa (id_kelas, npm)
SELECT id_kelas, '2021006' FROM kelas WHERE kode_kelas = 'BD-A-2024';

-- 9. Tugas Besar
INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas)
SELECT 'Tugas Besar RPL', 'Membuat aplikasi web menggunakan Spring Boot dan Thymeleaf', 
       '2024-09-01', '2024-12-15', id_kelas
FROM kelas WHERE kode_kelas = 'RPL-A-2024';

INSERT INTO tugas_besar (nama_tugas, deskripsi, tanggal_mulai, tanggal_selesai, id_kelas)
SELECT 'Tugas Besar Basis Data', 'Merancang dan implementasi database sistem informasi', 
       '2024-09-15', '2024-12-20', id_kelas
FROM kelas WHERE kode_kelas = 'BD-A-2024';

-- 10. Komponen Penilaian
INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Proposal', 15.0, 'Penilaian proposal: latar belakang, tujuan, metodologi', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Progress 1', 20.0, 'Penilaian progress 1: Use Case, Class Diagram, ERD', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Progress 2', 25.0, 'Penilaian progress 2: Implementasi backend dan frontend', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Demo Final', 40.0, 'Penilaian demo: kelengkapan fitur, UI/UX, presentasi', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar RPL';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Desain Database', 40.0, 'ERD, normalisasi, relasi antar tabel', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Implementasi SQL', 35.0, 'DDL, DML, query kompleks', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data';

INSERT INTO komponen_penilaian (nama_komponen, bobot, deskripsi_rubrik, id_tugas)
SELECT 'Presentasi', 25.0, 'Demo dan penjelasan teknis', id_tugas
FROM tugas_besar WHERE nama_tugas = 'Tugas Besar Basis Data';

-- 11. Kelompok
INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok 1', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-A-2024';

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok 2', 4, id_kelas FROM kelas WHERE kode_kelas = 'RPL-A-2024';

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok 1', 4, id_kelas FROM kelas WHERE kode_kelas = 'BD-A-2024';

INSERT INTO kelompok (nama_kelompok, maksimal_anggota, id_kelas)
SELECT 'Kelompok 2', 4, id_kelas FROM kelas WHERE kode_kelas = 'BD-A-2024';

-- 12. Anggota Kelompok
-- RPL Kelompok 1: mahasiswa 1 & 2
INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021001'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 1' AND kl.kode_kelas = 'RPL-A-2024';

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021002'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 1' AND kl.kode_kelas = 'RPL-A-2024';

-- RPL Kelompok 2: mahasiswa 3 & 4
INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021003'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 2' AND kl.kode_kelas = 'RPL-A-2024';

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021004'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 2' AND kl.kode_kelas = 'RPL-A-2024';

-- BD Kelompok 1: mahasiswa 3 & 4
INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021003'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 1' AND kl.kode_kelas = 'BD-A-2024';

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021004'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 1' AND kl.kode_kelas = 'BD-A-2024';

-- BD Kelompok 2: mahasiswa 5 & 6
INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021005'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 2' AND kl.kode_kelas = 'BD-A-2024';

INSERT INTO anggota_kelompok (id_kelompok, npm)
SELECT k.id_kelompok, '2021006'
FROM kelompok k JOIN kelas kl ON k.id_kelas = kl.id_kelas
WHERE k.nama_kelompok = 'Kelompok 2' AND kl.kode_kelas = 'BD-A-2024';

-- Done!
SELECT 'Database berhasil di-reset dengan data bersih!' as status;
