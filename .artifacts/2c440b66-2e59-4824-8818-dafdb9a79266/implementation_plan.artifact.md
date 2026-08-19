# Implementasi Statistik AUM dengan Donut & Bar Chart

Rencana ini bertujuan untuk membuat halaman statistik AUM yang menampilkan data distribusi bidang masalah (JDK, DPI, KHK, HSO, KDP, EDK, WSG, ANM, HMP, PDP) menggunakan grafik Donut dan Bar.

## User Review Required

> [!IMPORTANT]
> Saya akan menggunakan library **MPAndroidChart** untuk menampilkan grafik. Pastikan perangkat memiliki akses internet untuk mengunduh dependency ini dari JitPack.

## Proposed Changes

### Configuration

#### [MODIFY] [settings.gradle.kts](file:///D:/latihan_Project/Projct_BK_0.1/settings.gradle.kts)
Menambahkan repositori JitPack agar library MPAndroidChart dapat diunduh.

#### [MODIFY] [libs.versions.toml](file:///D:/latihan_Project/Projct_BK_0.1/gradle/libs.versions.toml)
Menambahkan versi dan definisi library MPAndroidChart.

#### [MODIFY] [build.gradle.kts](file:///D:/latihan_Project/Projct_BK_0.1/app/build.gradle.kts)
Mengimplementasikan library MPAndroidChart ke dalam project.

### UI / Layout

#### [MODIFY] [fragment_statistik_aum.xml](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/res/layout/fragment_statistik_aum.xml)
Mengubah layout fragment untuk menampung `PieChart` (sebagai Donut Chart) dan `BarChart`. Layout akan menggunakan `NestedScrollView` agar bisa di-scroll jika konten melebihi layar.

### Logic

#### [MODIFY] [StatistikAumFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/StatistikAumFragment.kt)
- Mengambil data hasil AUM melalui `AumServices`.
- Melakukan agregasi data untuk menghitung jumlah temuan masalah per kategori (JDK, DPI, dll).
- Mengatur data ke dalam `PieChart` dan `BarChart`.
- Mengimplementasikan ViewBinding untuk interaksi UI yang lebih mudah.

## Verification Plan

### Automated Tests
- Tidak ada tes otomatis baru, namun akan dipastikan build Gradle berhasil setelah penambahan dependency.

### Manual Verification
- Menjalankan aplikasi dan membuka halaman Statistik AUM.
- Memastikan Donut Chart menampilkan proporsi bidang masalah dengan benar.
- Memastikan Bar Chart menampilkan jumlah masalah per kategori dengan benar.
- Memastikan label kategori (JDK, DPI, dll) muncul dengan jelas.
