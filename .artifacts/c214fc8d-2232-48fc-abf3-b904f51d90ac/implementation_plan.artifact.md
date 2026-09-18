# Rencana Implementasi: Report Statistik Kuesioner & Sosiografik Per Kelas

Fitur ini akan memungkinkan Guru BK untuk melihat ringkasan hasil kuesioner dari seluruh siswa dalam satu kelas secara kolektif, baik dalam bentuk grafik (untuk pilihan ganda) maupun daftar (untuk esai).

## User Review Required

> [!IMPORTANT]
> **Metode Pengolahan Data:** Karena saat ini belum ada endpoint API khusus untuk agregat statistik kelas di `KuesionerServices`, implementasi awal akan menggunakan **Client-Side Aggregation**. Artinya, aplikasi akan mengambil semua jawaban siswa di kelas tersebut lalu menghitung statistiknya di HP.
>
> **Visualisasi:** Kita akan menggunakan library `MPAndroidChart` yang sudah ada di project untuk menampilkan grafik Pie Chart (persentase) dan Bar Chart (frekuensi).

## Proposed Changes

### 1. Data Layer (Models & Services)

#### [MODIFY] [KuesionerServices.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/KuesionerServices.kt)
Menambahkan endpoint atau memastikan endpoint yang ada bisa mengambil seluruh jawaban satu kelas (jika diperlukan sinkronisasi dengan backend di masa depan). Untuk sekarang, kita akan memanfaatkan `getJawabanSiswa` secara iteratif atau endpoint list responden.

### 2. UI Layer (Fragments & Adapters)

#### [NEW] [ReportKuesionerFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/ReportKuesionerFragment.kt)
Fragmen utama untuk menampilkan laporan. Akan berisi:
- Ringkasan jumlah responden vs total siswa.
- RecyclerView yang menampilkan item report per soal.

#### [NEW] [item_report_grafik.xml](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/res/layout/item_report_grafik.xml)
Layout untuk menampilkan soal pilihan ganda lengkap dengan PieChart.

#### [NEW] [item_report_esai.xml](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/res/layout/item_report_esai.xml)
Layout untuk menampilkan soal esai dengan daftar jawaban teks yang bisa di-expand.

#### [MODIFY] [RespondenKuesionerFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/RespondenKuesionerFragment.kt)
Menambahkan tombol "Lihat Statistik Kelas" di bagian header atau FAB untuk membuka `ReportKuesionerFragment`.

### 3. Logic Layer (Aggregation)

#### [NEW] [KuesionerAggregator.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/util/KuesionerAggregator.kt)
Utility class untuk:
- Menghitung frekuensi jawaban pilihan ganda.
- Mengelompokkan jawaban esai berdasarkan ID Soal.
- Menghitung persentase masalah (khusus untuk instrumen AUM).

---

## Verification Plan

### Automated Tests
- Membuat Unit Test untuk `KuesionerAggregator` guna memastikan perhitungan persentase dan frekuensi benar berdasarkan data dummy.

### Manual Verification
1. Login sebagai Guru BK.
2. Buka menu Kuesioner -> Pilih Kuesioner -> Pilih Kelas.
3. Klik tombol "Lihat Statistik Kelas".
4. Verifikasi bahwa grafik Pie Chart muncul untuk soal Pilihan Ganda.
5. Verifikasi bahwa daftar teks muncul untuk soal Esai.
6. Cek kesesuaian data antara jumlah responden dengan statistik yang tampil.
