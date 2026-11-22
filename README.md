# Samawa (Malakul Jibal) — Private Family Command Center 👨‍👩‍👧‍👦

Aplikasi Android modern berbasis **Jetpack Compose** dan **Clean Architecture** yang dirancang sebagai pusat kendali kehidupan keluarga Indonesia. Mengintegrasikan pencatatan keuangan harian, agenda terpadu, pemantauan tumbuh kembang anak berbasis standar WHO, brankas arsip dokumen penting, rekam medis darurat, dan profil keluarga dalam satu ekosistem privat yang aman dan *offline-first*.

---

## 🌟 Mengapa Samawa?

Sebagian besar keluarga mengelola urusan rumah tangga secara tercecer: jadwal di grup WhatsApp, struk belanja terlupakan, buku KIA/imunisasi anak rentan hilang, dan dokumen penting (KK, KTP, BPJS) panik dicari saat keadaan darurat.

**Samawa** menyelesaikan masalah ini dengan pendekatan:
1. **Input Cepat (< 5 detik)**: Catat pengeluaran dan kebutuhan harian dengan satu ketukan dari tombol aksi cepat (*Floating Action Button*).
2. **Offline-First & Tangguh**: Berfungsi penuh tanpa koneksi internet menggunakan penyimpanan lokal SQLite (Room Database), dan otomatis melakukan sinkronisasi latar belakang saat tersambung ke server.
3. **Privasi & Akses Berbasis Peran (RBAC)**: Menjaga kerahasiaan data sensitif (keuangan rumah tangga dan dokumen hukum) agar hanya dapat diakses oleh peran orang tua (*Super Admin / Parent*), sementara anggota keluarga lainnya tetap dapat melihat agenda bersama.
4. **Relevan untuk Keluarga Indonesia**: Dilengkapi kategori preset khas rumah tangga lokal (listrik/air, belanja dapur, pendidikan, sedekah/infaq), kalkulasi usia bayi presisi dalam bulan, dan kesiapan integrasi kalender Hijriyah serta zakat.

---

## 📱 Modul & Fitur Utama

```
┌─────────────────────────────────────────────────────────────┐
│                    SAMAWA COMMAND CENTER                    │
├──────────────┬──────────────┬──────────────┬────────────────┤
│ 💰 Keuangan  │ 📅 Agenda    │ 👶 Tumbuh    │ 📄 Brankas     │
│    Keluarga  │    Terpadu   │    Kembang   │    Dokumen     │
├──────────────┴──────────────┴──────────────┴────────────────┤
│ 🏥 Rekam Medis & Emergency ID  │ 👨‍👩‍👧‍👦 Profil & Silsilah       │
└────────────────────────────────┴─────────────────────────────┘
```

### 1. 💰 Keuangan & Anggaran Kas Keluarga (`FinancePreviewScreen`)
- **Pencatatan Kas Harian**: Catat pengeluaran dan pemasukan dengan cepat menggunakan kategori rekomendasi (Makan & Minum, Belanja Harian, Listrik & Utilitas, Pendidikan Anak, Kesehatan, Transportasi, Sedekah/Infaq).
- **Safe-to-Spend Real-Time**: Kalkulasi otomatis sisa saldo kas yang aman dibelanjakan setelah dikurangi cadangan tabungan wajib.
- **Peringatan Anggaran Pintar**: Indikator batas anggaran bulanan dengan sinyal peringatan 80% (*Cap Alert*) dan notifikasi over-budget.
- **Grafik Arus Kas 6 Bulan**: Visualisasi tren pemasukan vs pengeluaran keluarga.

### 2. 📅 Jadwal & Pengingat Keluarga Terpadu (`CalendarScreen`)
- **Kalender Multi-Kategori**: Jadwal periksa medis, imunisasi anak, perpanjangan dokumen, tenggat tagihan, dan acara keluarga.
- **Tambah Agenda Seketika**: Form interaktif untuk mencatat kegiatan, waktu pelaksanaan, lokasi, dan penugasan anggota keluarga.
- **Penyaringan Kategori**: Filter instan untuk memilah agenda prioritas.

### 3. 👶 Pemantauan Tumbuh Kembang Anak (`ChildDevelopmentPreviewScreen`)
- **Digitalisasi Buku KIA**: Pencatatan riwayat berat badan (kg), tinggi badan (cm), dan lingkar kepala (cm).
- **Standar Pertumbuhan WHO**: Perbandingan visual kurva tumbuh kembang merujuk pada *WHO Child Growth Standards*.
- **Tracking Milestone & Imunisasi**: Panduan jadwal imunisasi anak dan pemantauan tahapan perkembangan (motorik kasar, halus, bahasa, sosial).

### 4. 📄 Brankas Dokumen Keluarga (`DocumentsPreviewScreen`)
- **Katalog Arsip Lengkap**: Penyimpanan identitas Kartu Keluarga (KK), KTP, Akta Kelahiran, Buku Nikah, BPJS/Asuransi, Paspor, Ijazah, dan Sertifikat.
- **Pencarian Cepat & Filter Kategori**: Temukan nomor NIK, nomor BPJS, atau dokumen dalam hitungan detik.
- **Pengingat Masa Berlaku**: Notifikasi dokumen yang mendekati kedaluwarsa (misal: STNK, paspor).

### 5. 🏥 Kesehatan Keluarga & Kartu Darurat (`HealthPreviewScreen`)
- **Kartu Medis Darurat**: Akses cepat ke data golongan darah, alergi obat/makanan, dan kontak darurat keluarga.
- **Log Kunjungan Medis**: Riwayat diagnosis dokter, klinik/rumah sakit rujukan, serta anjuran resep obat.

### 6. 👨‍👩‍👧‍👦 Profil Anggota & Hak Akses (`FamilyProfilesScreen`)
- **Manajemen Profil**: Pengaturan nama lengkap, nama panggilan, tanggal lahir, dan penghitungan usia dinamis (Tahun & Bulan).
- **RBAC (Role-Based Access Control)**: Pemisahan peran *Super Admin*, *Parent*, dan *Member* untuk perlindungan privasi finansial dan medis.

### 7. 🏠 Dashboard Beranda "Hari Ini" (`HomeDashboardScreen`)
- **Pusat Aksi Cepat**: Tombol pintas untuk catat kas, buat agenda, ukur anak, dan buka arsip dokumen.
- **Agenda Terdekat & Sisa Kas**: Sekilas pandang informasi penting yang perlu diselesaikan hari ini.

---

## 🏗️ Arsitektur Teknis

Aplikasi dibangun mengikuti kaidah **Android Architecture Recommendations** dan **Clean Architecture**:

```
app/src/main/java/com/example/
├── core/
│   └── config/              # Konfigurasi endpoint API & status koneksi
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt   # Room Database SQLite
│   │   ├── SessionManager.kt # Enkripsi sesi JWT SharedPreferences
│   │   ├── dao/             # Data Access Objects (TransactionDao, DocumentDao, MedicalRecordDao)
│   │   └── entity/          # Room Entities
│   ├── mock/                # Mock data seed untuk offline demo mode
│   ├── remote/              # Retrofit 2 + Moshi API service & interceptors
│   └── sync/                # WorkManager untuk background periodic sync
├── domain/
│   ├── model/               # Model domain murni (Kotlin Data Classes)
│   ├── rbac/                # Rule-based access control engine
│   └── repository/          # Abstraksi antarmuka data
└── ui/
    ├── components/          # Reusable design system components
    ├── i18n/                # Sistem lokalisasi bilingual (Indonesia & English)
    ├── navigation/          # Jetpack Navigation Compose graph & shell
    ├── screens/             # 10 Layar modular deklaratif Compose
    ├── state/               # MainViewModel & MalakulUiState (StateFlow)
    └── theme/               # Material Theme 3 kustom (MalakulTheme, Color, Type)
```

---

## 🛠️ Prasyarat & Cara Menjalankan

### Persyaratan
- **Android Studio** Ladybug / Meerkat (atau versi lebih baru)
- **JDK 17** (Temurin / Zulu / OpenJDK)
- **Android SDK API 36** (Minimum SDK: 24 / Android 7.0)

### Langkah Build & Run

1. **Clone repository**:
   ```bash
   git clone https://github.com/malakuljibal/malakuljibal-app.git
   cd malakuljibal-app
   ```

2. **Jalankan Unit Test**:
   ```bash
   ./gradlew test
   ```

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   File APK hasil kompilasi akan berada di: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 Otomasi CI/CD

Repository ini dilengkapi dengan **GitHub Actions** (`.github/workflows/build-apk.yml`) yang secara otomatis:
- Mempersiapkan JDK 17 & Gradle 9.3.1.
- Menghasilkan debug keystore.
- Menguji integritas kode dan membangun berkas APK siap uji.
- Mengunggah artefak APK `samawa-app-debug` pada setiap push ke branch `main`.

---

## 📄 Lisensi
Hak Cipta © 2026 Keluarga Samawa / Malakul Jibal.
Dilisensikan untuk penggunaan manajemen keluarga pribadi dan komersial.
