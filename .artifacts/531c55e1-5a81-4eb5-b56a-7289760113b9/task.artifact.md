# Task List - Timeline UI Refinements

- [x] Update `TimelineRightPane.kt` to accept `firstDate` and `totalNumDays`, format date headers dynamically, and use `MaterialTheme.colorScheme.outlineVariant` instead of hard-coded `Color.LightGray`
- [x] Update `TimelineLeftPane.kt` to show two lines of text (Title on line 1, Assignee · Status on line 2) with ellipsis
- [x] Update `TimelineContent.kt` to remove the placeholder text, fix loading state / empty state / content branch order, place legend inside the chart branch, pass `firstDate` and `totalNumDays` to `TimelineRightPane`, add `Modifier.weight(1f)` to right pane, and unify sample data across previews
- [x] Build and verify with `app:assembleDebug` (`success=true`)
- [x] Create walkthrough artifact
