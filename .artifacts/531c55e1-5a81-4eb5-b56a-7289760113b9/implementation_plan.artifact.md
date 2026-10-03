# Implementation Plan - Timeline UI Refinements & Fixes

Refactor timeline UI components (`TimelineContent`, `TimelineRightPane`, `TimelineLeftPane`, `TimelineLegend`, and Previews) to address all "Must fix" and "Should fix" feedback items.

## User Review Required

> [!NOTE]
> No breaking changes or architecture alterations. This plan addresses UI layout correctness, M3 theming, dynamic date handling in the right pane, loading state management, and sample data unification.

## Proposed Changes

### Timeline Component Refinements

#### [MODIFY] [TimelineContent.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineContent.kt)
- Remove the placeholder `Text` element at the top.
- Implement correct branch ordering for loading state, empty state, and content:
  1. `uiState.isLoading` → show `CircularProgressIndicator` centered.
  2. `uiState.items.isEmpty()` → show `EmptyTimelineState`.
  3. `else` → show `TimelineLegend` and the horizontal/vertical scroll container with `TimelineLeftPane` and `TimelineRightPane`.
- Pass `firstDate` and `totalNumDays` from `uiState` into `TimelineRightPane`.
- Give `TimelineRightPane` `Modifier.weight(1f)` inside the row container.
- Create a single shared private sample `TimelineUiState` (with corrected Task 4 dates) and reuse it across previews.

#### [MODIFY] [TimelineRightPane.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineRightPane.kt)
- Accept `firstDate: LocalDate` and `totalNumDays: Int` as parameters.
- Replace hard-coded `(1..14)` with `(0 until totalNumDays)`.
- Format day header labels dynamically (e.g. `firstDate.plusDays(it.toLong()).format(DateTimeFormatter.ofPattern("d MMM"))` or similar).
- Replace hard-coded `Color.LightGray` with `MaterialTheme.colorScheme.outlineVariant`.

#### [MODIFY] [TimelineLeftPane.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineLeftPane.kt)
- Update row layout (height supports 2 lines in 56sp) to display two lines of text with ellipsis (`maxLines = 1`):
  - Line 1: Task title.
  - Line 2: `${item.assigneeName} · ${formatStatus(item.status)}`.

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify zero compilation or syntax errors.

### Manual Verification
- Review Compose Previews (`TimelineContentPreview`, `TimelineContentLoadingPreview`, `TimelineContentEmptyPreview`, `TimelineContentSkeletonPreview`) to verify proper loading spinner, empty state, legend placement, date headers, and two-line task labels.
