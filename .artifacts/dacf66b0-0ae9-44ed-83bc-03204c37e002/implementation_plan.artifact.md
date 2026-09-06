# Debugging AUM Data Mismatch

This plan addresses the issue where clicking a student in the AUM list displays data for a different student. We will add diagnostic Toast messages and logging to identify whether the source of the error is the Frontend (passing the wrong ID) or the Backend (returning the wrong data for a given ID).

## User Review Required

> [!IMPORTANT]
> The diagnostic Toast will appear every time the AUM detail is loaded. This is intended for debugging and can be removed once the root cause is identified.

## Proposed Changes

### BK Feature

#### [MODIFY] [DetailAumSiswaFragment.kt](file:///D:/latihan_Project/Projct_BK_0.1/app/src/main/java/com/example/bknova/fragment/DetailAumSiswaFragment.kt)

- Add diagnostic Toast in `displayData` to compare requested student info (from arguments) with received student info (from API).
- Add logging to track which API/fallback method was successful.
- Refine error messages to include status codes.

## Verification Plan

### Manual Verification
1. Open the "Data AUM Siswa" list.
2. Click on a student (e.g., BIMBIM RONTI).
3. Observe the Toast message:
   - If it says "Frontend passed wrong ID", the adapter logic needs fixing.
   - If it says "Backend returned wrong data", the API or database mapping needs fixing.
   - If it says "Data Match", the issue might be intermittent or related to specific data states.
