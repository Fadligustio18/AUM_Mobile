# Redesign Home BK Layout

Redesign the Guru BK home screen to match the provided design image, including a new "Instrumen konseling" section title and a specific grid/list layout for cards.

## User Review Required

> [!IMPORTANT]
> - The "Gaya Belajar" card from the previous layout is removed to match the provided image.
> - A new "Tiket Konseling" card is added with a red notification badge.
> - The cards are rearranged: Row 1 has two cards (Sosiografik, Data AUM), Row 2 has one full-width card (Tiket Konseling), and Row 3 has one full-width card (Data Siswa).

## Proposed Changes

### Resources

#### [NEW] [bg_badge_red_circle.xml](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/res/drawable/bg_badge_red_circle.xml)
Create a circular red background for the notification badge.

### Layout

#### [MODIFY] [fragment_home_bk.xml](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/res/layout/fragment_home_bk.xml)
- Change section title to "Instrumen konseling".
- Implement the new grid/list structure.
- Add notification badge to "Tiket Konseling" card.
- Align icon colors and tints with the provided image.

### Code

#### [MODIFY] [homeBkFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/homeBkFragment.kt)
- Update view bindings for the new card structure.
- Remove "Gaya Belajar" logic.
- Add click listener for "Tiket Konseling".

## Verification Plan

### Automated Tests
- Build the project to ensure no XML or Kotlin compilation errors.
- `gradlew app:assembleDebug`

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to the Guru BK Home screen.
- Verify the layout matches the image (card arrangement, title, colors, and badge).
- Test click listeners for all four cards (Sosiografik, Data AUM, Tiket Konseling, Data Siswa).
