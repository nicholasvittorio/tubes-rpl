-- =====================================================
-- DATABASE SCHEMA FOR MANAJEMEN TUGAS BESAR
-- PostgreSQL Version (Revised to match ERD)
-- =====================================================

-- 1. Tabel Pengguna
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    peran VARCHAR(20) NOT NULL CHECK (peran IN ('admin', 'dosen', 'mahasiswa')), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabel Admin
CREATE TABLE IF NOT EXISTS admin (
    id_pengguna INT PRIMARY KEY,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 3. Tabel Dosen
CREATE TABLE IF NOT EXISTS dosen (
    nip VARCHAR(20) PRIMARY KEY, 
    id_pengguna INT NOT NULL UNIQUE,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 4. Tabel Mahasiswa
CREATE TABLE IF NOT EXISTS mahasiswa (
    npm VARCHAR(20) PRIMARY KEY, 
    id_pengguna INT NOT NULL UNIQUE, 
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE
);

-- 5. Tabel Mata Kuliah
CREATE TABLE IF NOT EXISTS mata_kuliah (
    id_matkul SERIAL PRIMARY KEY,
    kode_matkul VARCHAR(20) NOT NULL UNIQUE,
    nama_matkul VARCHAR(100) NOT NULL,
    sks INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Tabel Kelas 
CREATE TABLE IF NOT EXISTS kelas (
    id_kelas SERIAL PRIMARY KEY,
    nama_kelas VARCHAR(50) NOT NULL,
    kode_kelas VARCHAR(20) NOT NULL UNIQUE,
    id_matkul INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE
);

-- 7. Tabel Bridge Kelas-Dosen
CREATE TABLE IF NOT EXISTS kelas_dosen (
    id_kelas INT,
    nip VARCHAR(20),
    PRIMARY KEY (id_kelas, nip),
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    FOREIGN KEY (nip) REFERENCES dosen(nip) ON DELETE CASCADE
);

-- 8. Tabel Bridge Kelas-Mahasiswa
CREATE TABLE IF NOT EXISTS kelas_mahasiswa (
    id_kelas INT,
    npm VARCHAR(20),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_kelas, npm),
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    FOREIGN KEY (npm) REFERENCES mahasiswa(npm) ON DELETE CASCADE
);

-- 9. Tabel Tugas Besar
-- Added unique constraint on (nama_tugas, id_kelas) to prevent duplicates
CREATE TABLE IF NOT EXISTS tugas_besar (
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

-- 10. Tabel Komponen Penilaian (Rubrik) 
-- Added unique constraint on (nama_komponen, id_tugas) to prevent duplicates
CREATE TABLE IF NOT EXISTS komponen_penilaian (
    id_komponen SERIAL PRIMARY KEY,
    nama_komponen VARCHAR(100) NOT NULL,
    bobot FLOAT NOT NULL,
    deskripsi_rubrik TEXT,
    id_tugas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tugas) REFERENCES tugas_besar(id_tugas) ON DELETE CASCADE,
    UNIQUE(nama_komponen, id_tugas)
);

-- 11. Tabel Kelompok
-- Added unique constraint on (nama_kelompok, id_kelas) to prevent duplicates
CREATE TABLE IF NOT EXISTS kelompok (
    id_kelompok SERIAL PRIMARY KEY,
    nama_kelompok VARCHAR(50) NOT NULL,
    maksimal_anggota INT DEFAULT 4,
    id_kelas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_kelas) REFERENCES kelas(id_kelas) ON DELETE CASCADE,
    UNIQUE(nama_kelompok, id_kelas)
);

-- 12. Tabel Anggota Kelompok
CREATE TABLE IF NOT EXISTS anggota_kelompok (
    id_kelompok INT,
    npm VARCHAR(20),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_kelompok, npm),
    FOREIGN KEY (id_kelompok) REFERENCES kelompok(id_kelompok) ON DELETE CASCADE,
    FOREIGN KEY (npm) REFERENCES mahasiswa(npm) ON DELETE CASCADE
);

-- 13. Tabel Nilai
CREATE TABLE IF NOT EXISTS nilai (
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

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_pengguna_username ON pengguna(username);
CREATE INDEX IF NOT EXISTS idx_pengguna_peran ON pengguna(peran);
CREATE INDEX IF NOT EXISTS idx_kelas_kode ON kelas(kode_kelas);
CREATE INDEX IF NOT EXISTS idx_nilai_npm ON nilai(npm);
CREATE INDEX IF NOT EXISTS idx_nilai_komponen ON nilai(id_komponen);
