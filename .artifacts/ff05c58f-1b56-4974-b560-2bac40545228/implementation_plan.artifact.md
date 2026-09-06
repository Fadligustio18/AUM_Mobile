# Konfirmasi Dialog untuk Aksi Destruktif Tiket

Menambahkan dialog konfirmasi (peringatan) sebelum pengguna (Siswa atau Guru BK) menghapus tiket, menghapus riwayat, atau membatalkan tiket. Hal ini bertujuan untuk mencegah penghapusan data secara tidak sengaja.

## User Review Required

> [!NOTE]
> Dialog konfirmasi akan menggunakan `MaterialAlertDialogBuilder` standar untuk menjaga konsistensi dengan UI yang sudah ada. Teks dalam dialog akan menyesuaikan dengan konteks aksi (Hapus Tiket vs Batalkan Tiket vs Hapus Riwayat).

## Proposed Changes

### [Component Name] Detail Tiket

#### [MODIFY] [DetailTiketFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DetailTiketFragment.kt)

- Mengubah logika `hapusTiketSiswa()` agar memunculkan `MaterialAlertDialogBuilder` terlebih dahulu.
- Membuat fungsi baru `performDeleteTiket()` untuk mengeksekusi panggilan API penghapusan setelah dikonfirmasi.
- Menyesuaikan Judul dan Pesan dialog berdasarkan status tiket saat ini (misalnya: status "Disetujui" akan menampilkan pesan "Batalkan Tiket").

## Verification Plan

### Manual Verification
- Buka detail tiket sebagai **Siswa** dengan status "Dikirim", klik "Hapus Tiket", pastikan dialog muncul.
- Buka detail tiket sebagai **Siswa** dengan status "Disetujui", klik "Batalkan Tiket", pastikan dialog muncul dengan pesan pembatalan.
- Buka detail tiket sebagai **Guru BK** dengan status "Selesai", klik "Hapus Riwayat", pastikan dialog muncul.
- Cek tombol "Batal" di dialog untuk memastikan tiket TIDAK terhapus.
- Cek tombol "Ya, Lanjutkan" untuk memastikan tiket terhapus dan kembali ke halaman daftar.
