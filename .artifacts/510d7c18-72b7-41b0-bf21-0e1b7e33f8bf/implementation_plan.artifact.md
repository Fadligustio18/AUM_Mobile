# Fix Unresolved Reference and Sync BK Navigation

Fix the `setBottomNavigationVisibility` unresolved reference error in `SuccessAumFragment` and unify the navigation logic between Student and BK activities.

## Proposed Changes

### [Activity]

#### [MODIFY] [halaman_siswa_Activity.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/activity/halaman_siswa_Activity.kt)
- Add `setBottomNavigationVisibility(isVisible: Boolean)` method to allow fragments to hide/show the navigation bar.

#### [MODIFY] [guruBkActivity.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/activity/guruBkActivity.kt)
- Ensure the navigation visibility logic is consistent.

### [Fragment]

#### [MODIFY] [SuccessAumFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/SuccessAumFragment.kt)
- Update `onResume` and `onDestroyView` to handle both `halaman_siswa_Activity` and `guruBkActivity` when toggling navigation visibility.

## Verification Plan

### Automated Tests
- Build the project using `./gradlew :app:compileDebugKotlin` to ensure the unresolved reference is gone.

### Manual Verification
- Deploy the app.
- Open `SuccessAumFragment` in both Student and BK roles.
- Verify that the bottom navigation bar is hidden when the fragment is shown and reappears when it is dismissed.
