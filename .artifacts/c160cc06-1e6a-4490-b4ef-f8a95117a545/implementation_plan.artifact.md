# Implementation Plan - BkNova Refactor

Refactor BkNova to include dedicated features for Admin, Guru BK, and Siswa, focusing on AUM, Sosiografik, and Counseling Tickets.

## User Review Required

> [!IMPORTANT]
> - The API base URL is currently set to `http://192.168.69.50:3000/` in `Aktor.kt`. Please ensure the backend server is updated to support the new endpoints for Tickets and Sosiografik.
> - The role-based navigation logic in the Login flow needs to be verified to ensure users are directed to the correct dashboards (Admin, Guru BK, or Siswa).

## Proposed Changes

### Data Models

#### [NEW] [TiketModels.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/model/TiketModels.kt)
- Define `Tiket` data class with fields: `id`, `siswa`, `kelas`, `jurusan`, `judul`, `isi`, `status`, `tempat`, `tanggalPerjanjian`, `tanggalPembuatan`.
- Define request/response models for Ticket actions (Approve, Postpone, etc.).

#### [NEW] [SociografikModels.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/model/SociografikModels.kt)
- Define models for Sosiografik questions and student answers.

#### [NEW] [AdminModels.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/model/AdminModels.kt)
- Define models for `Kelas` and `User` (for CRUD operations).

---

### API Services

#### [NEW] [TiketServices.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/TiketServices.kt)
- Implement endpoints based on the provided image:
    - `GET /api/v1/tiket/bk/{idUser}`
    - `PATCH /api/v1/tiket/bk/setujui/{idTiket}`
    - `PATCH /api/v1/tiket/bk/lokasi/{idTiket}`
    - `PATCH /api/v1/tiket/bk/tunda/{idTiket}`
    - `PATCH /api/v1/tiket/bk/batalkan/{idTiket}`
    - `PATCH /api/v1/tiket/bk/selesai/{idTiket}`
- Add Siswa submission endpoint: `POST /api/v1/tiket/siswa/pengajuan`.

#### [NEW] [SociografikServices.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/SociografikServices.kt)
- CRUD operations for Guru BK.
- Submission for Siswa.

#### [NEW] [AdminServices.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/AdminServices.kt)
- CRUD for Users and Classes.

#### [MODIFY] [Aktor.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/service/Aktor.kt)
- Register the new services (`tiket`, `sociografik`, `admin`).

---

### UI Components

#### Admin Features
- [NEW] `AdminActivity`: Main entry for Admin.
- [NEW] `UserManagementFragment`: CRUD UI for Users.
- [NEW] `KelasManagementFragment`: CRUD UI for Classes.

#### Guru BK Features
- [MODIFY] `homeBkFragment.kt`: Add/Update cards for Sosiografik and Tiket Konseling.
- [NEW] `DaftarTiketFragment`: List of incoming counseling tickets.
- [NEW] `DetailTiketFragment`: Detailed view with actions (Approve, Postpone, etc.).
- [NEW] `ManageSociografikFragment`: CRUD UI for Sosiografik questions.

#### Siswa Features
- [MODIFY] `homeSiswaFragment.kt`: Add/Update cards for Sosiografik and Tiket Konseling.
- [NEW] `PengajuanTiketFragment`: Form to submit a new counseling ticket.
- [NEW] `IsiSociografikFragment`: Form to answer Sosiografik questions.

## Verification Plan

### Automated Tests
- Unit tests for Model parsing (Gson).
- Integration tests for API Services using MockWebServer (if available) or manual testing against the dev server.

### Manual Verification
1. Login as **Admin**: Verify User and Kelas CRUD operations.
2. Login as **Guru BK**:
    - Verify list of tickets is displayed.
    - Test "Setujui" action with schedule and place.
    - Verify Sosiografik management.
3. Login as **Siswa**:
    - Submit a new Counseling Ticket.
    - Fill out AUM and Sosiografik.
    - Check ticket status.
