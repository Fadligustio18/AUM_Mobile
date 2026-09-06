# Implementasi Pull-to-Refresh di Berbagai Halaman

Rencana ini bertujuan untuk menambahkan fitur Pull-to-Refresh menggunakan `SwipeRefreshLayout` pada halaman-halaman utama aplikasi agar pengguna dapat memperbarui data tanpa harus keluar masuk halaman.

## Perubahan yang Diusulkan

### Dependensi
Menambahkan library `androidx.swiperefreshlayout:swiperefreshlayout` ke dalam proyek. (Sudah dilakukan)

### Halaman Guru BK (BkNova)
Menambahkan Pull-to-Refresh pada:
1.  **Daftar Tiket**: [DaftarTiketFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DaftarTiketFragment.kt) (Sudah dilakukan)
2.  **Daftar Kelas**: [DaftarKelasBkFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DaftarKelasBkFragment.kt)
3.  **Daftar Siswa**: [DaftarSiswaBkFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DaftarSiswaBkFragment.kt)
4.  **Home Guru**: [homeBkFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/homeBkFragment.kt)

### Halaman Siswa
Menambahkan Pull-to-Refresh pada:
1.  **Home Siswa**: [homeSiswaFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/homeSiswaFragment.kt)
2.  **Daftar Kuesioner**: [DaftarKuesionerSiswaFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DaftarKuesionerSiswaFragment.kt)
3.  **Daftar Tiket Siswa**: [DaftarTiketSiswaFragment](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DaftarTiketSiswaFragment.kt)

## Langkah-langkah Teknis
Untuk setiap halaman:
1.  **XML Layout**: Membungkus `RecyclerView` atau `NestedScrollView` dengan `SwipeRefreshLayout`.
2.  **Fragment Code**:
    *   Menginisialisasi `SwipeRefreshLayout`.
    *   Menambahkan `OnRefreshListener` yang memanggil fungsi pemuatan data.
    *   Mengatur `isRefreshing = false` setelah data selesai dimuat (berhasil atau gagal).

## Rencana Verifikasi
### Uji Coba Manual
1.  Buka setiap halaman yang telah dimodifikasi.
2.  Tarik layar ke bawah (Swipe Down).
3.  Pastikan indikator loading muncul.
4.  Pastikan data diperbarui dan indikator loading hilang setelah proses selesai.
