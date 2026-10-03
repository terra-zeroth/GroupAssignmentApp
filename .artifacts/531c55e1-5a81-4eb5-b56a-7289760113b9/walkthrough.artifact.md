# Walkthrough - Timeline UI Refinements & Fixes

Completed all "Must fix" and "Should fix" feedback items for the timeline feature.

## Changes

### [TimelineContent.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineContent.kt)
- Removed the placeholder `Text` component.
- Implemented correct branch ordering:
  1. `uiState.isLoading` → displays a centered `CircularProgressIndicator`.
  2. `uiState.items.isEmpty()` → displays `EmptyTimelineState`.
  3. `else` → displays `TimelineLegend` followed by the vertical scroll container with `TimelineLeftPane` and `TimelineRightPane`.
- Passed `firstDate` and `totalNumDays` into `TimelineRightPane`.
- Added `Modifier.weight(1f)` to `TimelineRightPane` to explicitly occupy remaining width.
- Created a single shared private sample `TimelineUiState` (`sampleTimelineUiState`) with valid task dates and reused it across all Previews.

### [TimelineRightPane.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineRightPlane.kt)
- Updated to accept `firstDate: LocalDate` and `totalNumDays: Int`.
- Replaced hard-coded `(1..14)` day loop with `(0 until totalNumDays)`.
- Dynamically formatted header dates using `DateTimeFormatter.ofPattern("d MMM")`.
- Replaced hard-coded `Color.LightGray` with theme-aware `MaterialTheme.colorScheme.outlineVariant`.

### [TimelineLeftPane.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/timeline/ui/TimelineLeftPane.kt)
- Updated task row layout to render two lines within the 56sp height:
  - Line 1: Task title (with ellipsis).
  - Line 2: `${item.assigneeName} · ${statusText}` (with ellipsis, using `bodySmall` and `onSurfaceVariant` color).

### [TaskModels.kt](file:///C:/Projects/MAD-Assignment/GroupAssignmentApp/app/src/main/java/com/example/groupprojectapp/tasks/data/TaskModels.kt)
- Ensured `TaskStatus` includes `BLOCKED` alongside `TODO`, `IN_PROGRESS`, and `DONE`.

## Verification Results

### Automated Tests
- Ran `gradle_build("app:assembleDebug")` → **Build finished successfully.**
