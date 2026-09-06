# Implementasi Notifikasi Sistem (FCM) untuk Tiket Konseling

Rencana ini bertujuan untuk menambahkan notifikasi sistem (push notification) yang muncul di status bar HP ketika ada perubahan status tiket konseling atau pengiriman tiket baru.

## User Review Required

> [!IMPORTANT]
> Notifikasi antar perangkat (Siswa ke Guru BK) memerlukan **Backend Server** dan **Firebase Cloud Messaging (FCM)**. Aplikasi Android tidak bisa mengirim notifikasi langsung ke perangkat lain tanpa perantara server.

## Proposed Changes

### 1. Konfigurasi Dependency & Firebase

#### [MODIFY] [app/build.gradle.kts](file:///D:/latihan_Project/Projct_BK_0.1/app/build.gradle.kts)
* Menambahkan dependency Firebase Cloud Messaging.

### 2. Implementasi Layanan Notifikasi

#### [NEW] [NotificationHelper.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/util/NotificationHelper.kt)
* Helper class untuk membuat Notification Channel dan menampilkan notifikasi.

#### [NEW] [MyFirebaseMessagingService.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/MyFirebaseMessagingService.kt)
* Service yang berjalan di background untuk menerima pesan dari Firebase.

### 3. Pengelolaan Token Perangkat

#### [MODIFY] [SessionManager.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/SessionManager.kt)
* Menambahkan fungsi untuk menyimpan FCM Token.

#### [MODIFY] [TiketServices.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/TiketServices.kt)
* Menambahkan endpoint untuk mengirim/update FCM Token ke server.

## Verification Plan

### Manual Verification
1. Jalankan aplikasi dan pastikan FCM Token berhasil didapatkan.
2. Simulasikan pengiriman pesan dari Firebase Console (Test Cloud Messaging).
3. Pastikan notifikasi muncul di HP dengan ikon dan suara yang sesuai.
4. Klik notifikasi dan pastikan membuka aplikasi ke halaman Tiket yang relevan.
