# GroupAssignmentApp
Building an Android app in Kotlin with Jetpack Compose that helps students that have a group project assignment organise their tasks and timeline etc. Helps manage group work.

## Team

| Aliyah Richshaine Macawile | 22072366 |
|----------------------------|----------|
| Twisha Domadia             | 22875086 |
| Manasviba Chauhan          | 23127979 |
|----------------------------|----------|

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

**2) Account login and creation**  
Email sign-up, log in from any phone (e.g. Firebase)
  
**Why it's out of Scope:**  
Needs a backend service not covered in the unit. Since all other data is local, online accounts wouldn't let teammates share tasks anyway, many security implications to consider

---
 
## Features
 
**Twisha:**  
project framework (navigation, MVVM, Room), local login with hashed passwords, settings, GitHub activity feed (R4)
 
**Manasviba:**  
to-do list, add/edit task form, task detail, job allocation with "my tasks" filter, deadline logic (days remaining, overdue, PERT), MoSCoW priority
 
**Aliyah:**  
Gantt timeline with today line, documentation log, screenshot attachment via photo picker (R6), timeline → documentation link, Material 3 theme 
 
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
 
**Permissions:** the app declares only `INTERNET` (for the GitHub feature). Attaching screenshots uses Android's photo picker, which needs no storage permission.

### Libraries

All libraries are downloaded automatically during Gradle sync. Versions without a number are set by the Compose BOM or in `gradle/libs.versions.toml`.

| Library | Version | Used for |
|---------|---------|----------|
| Jetpack Compose (BOM) and Material 3 | set by the BOM | All screens and the app theme |
| Material Icons Extended | set by the BOM | Icons (back, sort, filter, add) |
| Navigation Compose | 2.10.2 | Tasks list to task detail navigation |
| Lifecycle ViewModel Compose | 2.11.0 | ViewModels |
| Room (runtime, ktx, KSP compiler) | 2.8.5 | Local database for tasks, members and documentation entries |
| DataStore Preferences | 1.2.1 | Saved task sort order |
| Coil | 3.3.0 | Showing documentation screenshots |
| Retrofit and Gson converter | 2.11.0 | GitHub REST API (commit list) |

No API key is needed to build the project. The GitHub feature calls the public GitHub REST API without logging in, so no personal access token is required. This means only **public** repositories can be shown (a private repository shows an error) and GitHub limits how many unauthenticated requests can be made per hour.

 
## Run steps
 
### Emulator used for testing
| Setting | Value |
|---------|-------|
| Device profile | Medium Phone (6.4", 1080 × 2400, 420 dpi) |
| System image | Google Play Intel x86_64 Atom System Image |
| API level | **API 37.0 "CinnamonBun" android 17.0** |
| Services | Google Play Store |
 
### To run

1. **Create the emulator if you don't have it.**
   Go to *Tools → Device Manager → + (Create Virtual Device)*. Pick **Medium Phone**, choose the **API 37.0, Google Play Intel x86_64** system image (download it if prompted), then click **Finish**.
2. **Select the device.**
   In the toolbar device dropdown, choose the emulator (or a real phone running Android 8.1 or later with USB debugging enabled).
3. **Run.**
   Make sure the run configuration is **app**, then click the green **Run ▶** button.
4. **First launch.**
   Log in with the built-in demo account:

   | Field | Value |
   |-------|-------|
   | Group name | `Group 1` (any text works; it is not checked) |
   | Username | `Test` |
   | Password | `test` |

The username and password are case-sensitive (capital **T** in the username, all lowercase in the password). The list of assignees is created from the built-in accounts the first time the app runs. Tasks and documentation entries can then be added from inside the app.

### Using the GitHub feature

The GitHub screen shows the recent commits of a repository. It needs the **exact GitHub owner and repository name** and the repository must be **public**. You do not need to own the repository and no link is needed.

1. From the dashboard, open **Settings**.
2. Tap **Setup GitHub Repository**.
3. Enter the **GitHub Owner** and **Repository Name**, then tap **Save**. The app returns to Settings.
4. Tap Back to return to the dashboard, then open **[GitHub button label]**. The commits load.

Both fields are required. If either is left blank, the setup screen shows "Please enter a valid owner and repository."

The details are not saved when the app is closed, so enter them again after restarting.

**Test repositories**

| Case | GitHub Owner | Repository Name | Expected result |
|------|--------------|-----------------|-----------------|
| Working repository | `terra-zeroth` | `GroupAssignmentApp` | List of recent commits (message, author, date) |
| Empty repository (error case) | `lilicilia` | `MAD-EmptyRepoTest` | An error message, e.g. "Failed to load commits: HTTP 409" |
| Nothing entered yet | (blank) | (blank) | "Please set up your GitHub repository in Settings." |

An incorrect owner or repository name also shows an error (HTTP 404). With no internet connection, an error message appears with a different reason and the rest of the app still works.

---

## Architecture

The application follows the MVVM pattern with a repository layer for its main features: Tasks, Timeline and Documentation. In these, screens are composable functions that draw a single UiState object exposed by a ViewModel where it obtains data through repositories, which reads from Room or DataStore. Three parts do not follow this patter. The Login ViewModel, which checks a hardcoded list of accounts (UserData.kt) instead of using a repository. Home reads the task repository directly in the Screen. 

Currently Settings and GitHub have no ViewModel, instead the Settings values are held in Main Activity and passed into a stateless screen and the GitHub screen calls the Retrofit API directly and keeps its results in the composable.

