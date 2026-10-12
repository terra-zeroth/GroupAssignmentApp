# GroupAssignmentApp
Building an Android app in Kotlin with Jetpack Compose that helps students that have a group project assignment organise their tasks and timeline etc. Helps manage group work.

## Team
 
| Name | Student ID |
|------|------------|
| Aliyah Richshaine Macawile | 22072366 |
| Twisha Domadia | 22875086 |
| Manasviba Chauhan | 23127979 |
 
---

## App description

## Users:
Computing and engineering students working on group assignments. They currently coordinate across scattered tools (group chats, shared docs or spreadsheets, GitHub, memory), making it easy to lose track of who owns which task and when things are due

## Core task
Help a student project team allocate tasks to members and track them against key project deadlines in one place

## Data
- **Members**: local accounts (username and hashed password), stored in Room on the device.
- **Tasks**: each assigned to one member, with dates, estimates, status and MoSCoW priority.
- **Documentation entries**: dated notes and screenshots linked to timeline items.
Relationship: **one member → many tasks**.

## Out of scope
**1) Real-time group chat:**  
Teammates message each other live from their own phones.  
  
**Why it's out of Scope:**  
Needs a backend server and syncing between phones. Room stores data on one phone only and the spec says syncing isn't required

**2) Full calendar integration:**  
Syncing with Google Calendar and managing students' whole uni schedule  
  
**Why it's out of Scope:**  
The app focuses on one project's key dates, which the timeline already covers

---
 
## Features
 
**Twisha:**  
project framework (navigation, MVVM, Room), Material 3 theme with dark mode, local login with hashed passwords, settings (DataStore), GitHub activity feed (R4)
 
**Manasviba:**  
to-do list, add/edit task form, task detail, job allocation with "my tasks" filter, deadline logic (days remaining, overdue, PERT), MoSCoW priority
 
**Aliyah:**  
Gantt timeline with today line, documentation log, screenshot attachment via photo picker (R6), timeline → documentation link
 
---

## Requirements
 
| Requirement | Version |
|-------------|---------|
| Android Studio | **Quail 2, 2026.1.2 Patch 1** (Build #AI-261.25134.95.2612.15914620) |
| JDK (Gradle JDK) | **JDK 21** (the JetBrains Runtime bundled with Android Studio works) |
| Java source/target compatibility | Java 11 (set in `build.gradle.kts`, no action needed) |
| compileSdk | **API 37.1** (Android 17) |
| targetSdk | API 36 |
| minSdk | API 27 (Android 8.1) |
| Kotlin / KSP | KSP `2.2.10-2.0.2` (downloaded by Gradle automatically) |
| Internet connection | Needed for the first Gradle sync and for the GitHub activity feature |
 
Main libraries (downloaded automatically during Gradle sync): Jetpack Compose (BOM), Material 3, Navigation Compose 2.10.2, Lifecycle ViewModel Compose 2.11.0, Room 2.8.5, DataStore Preferences 1.2.1.
 
No API key is needed to build the project. [TODO] `[Confirm: add a note here if the GitHub feature needs a personal access token, e.g. if the repo is private.]`
 
## Build steps
 
1. **Extract the project.**  
   Unzip the submitted file into a folder. Avoid paths that contain spaces or are very long.
3. **Open it in Android Studio.**  
   Go to *File → Open*, select the extracted project folder (the one containing `settings.gradle.kts`), then click **OK**. Choose **Trust Project** if asked.
5. **Check the Gradle JDK.**  
   Go to *File → Settings → Build, Execution, Deployment → Build Tools → Gradle*. Make sure **Gradle JDK** is set to a JDK 21 (for example *jbr-21*), then click **Apply**.
7. **Install the Android 17 SDK if it's missing.**  
   Go to *Tools → SDK Manager → SDK Platforms*. Tick **Android 17.0 ("CinnamonBun")**, making sure API 37.1 is included, then click **Apply**.
9. **Sync Gradle.**  
    Click **Sync Project with Gradle Files** (the elephant icon in the top-right toolbar). Wait for it to finish without errors. The first sync downloads dependencies and can take a few minutes.
11. **Build.**  
    Go to *Build → Assemble Project*, or simply press Run in the next section, which builds automatically.

---
 
## Run steps
 
### Emulator used for testing
| Setting | Value |
|---------|-------|
| Device profile | Medium Phone (6.4", 1080 × 2400, 420 dpi) |
| System image | Google Play Intel x86_64 Atom System Image |
| API level | **API 37.0 "CinnamonBun", Android 17.0** |
| Services | Google Play Store |
 
### To run
1. **Create the emulator if you don't have it.**  
   Go to *Tools → Device Manager → + (Create Virtual Device)*. Pick **Medium Phone**, choose the **API 37.0, Google Play Intel x86_64** system image (download it if prompted), then click **Finish**.
3. **Select the device.**  
   In the toolbar device dropdown, choose the emulator (or a real phone running Android 8.1 or later with USB debugging enabled).
5. **Run.**  
   Make sure the run configuration is **app**, then click the green **Run ▶** button.
7. **First launch.**  
   `[Confirm: either "Tap Create account and register a username and password, then log in" OR "Log in with the demo account: username ___ / password ___".]` Tasks and documentation entries can then be added from inside the app.
---
