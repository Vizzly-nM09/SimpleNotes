# SimpleNotes

A clean, native Android note-taking application built with Java, demonstrating modern Model-View-ViewModel (MVVM) architecture with Room Database, LiveData, and Material 3 components.

---

## Features

- **Create and Edit Notes**: Quick note entry with title, body text, and automated timestamp tracking.
- **Reactive Data Layer**: Real-time list updates powered by Room Database and LiveData.
- **Search Notes**: Live search filter by title or content directly from the toolbar.
- **Swipe to Delete**: Swipe note cards left or right to dismiss, with an immediate Undo snackbar.
- **Safe Deletion**: Confirmation dialogs for deleting individual notes or clearing all notes at once.
- **Empty State**: Clear prompt and visual indicator when no notes are present.
- **Light and Dark Themes**: Native Material 3 DayNight support that adapts to system display settings.

---

## Architecture

This project follows the official Android Architecture recommendations using the **MVVM (Model-View-ViewModel)** pattern with an independent **Repository** layer:

```
[ View Layer ]            MainActivity / AddEditNoteActivity
       │                     (Observes LiveData, handles user interactions)
       ▼
[ ViewModel Layer ]       NoteViewModel
       │                     (Retains UI state across configuration changes)
       ▼
[ Repository Layer ]      NoteRepository
       │                     (Single source of truth, manages background threads)
       ▼
[ Data Layer ]            NoteDao -> Room Database (SQLite)
```

### Key Components

- **Room Database (`NoteDatabase`, `NoteDao`, `Note`)**: SQLite abstraction layer for local persistence, thread-safe access, and reactive query streams.
- **Repository (`NoteRepository`)**: Mediates between data sources and UI, delegating disk operations to background worker threads.
- **ViewModel (`NoteViewModel`)**: Holds and manages UI-related data in a lifecycle-conscious way, surviving configuration changes like screen rotations.
- **Adapter (`NoteAdapter`)**: Built on `ListAdapter` and `DiffUtil` for smooth item animations without reloading the entire list.

---

## Tech Stack

- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 37
- **Database**: AndroidX Room 2.6.1
- **Architecture**: AndroidX Lifecycle (ViewModel & LiveData) 2.8.7
- **UI Components**: Material Components for Android (Material 3), RecyclerView, CoordinatorLayout, ConstraintLayout
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`)

---

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/simplenotes/
│   ├── MainActivity.java             # Main notes list, search, and swipe actions
│   ├── AddEditNoteActivity.java      # Note editor for creating and updating
│   ├── adapter/
│   │   └── NoteAdapter.java          # ListAdapter with DiffUtil and date formatting
│   ├── database/
│   │   ├── Note.java                 # Room entity representing notes table
│   │   ├── NoteDao.java              # Data Access Object with LiveData queries
│   │   └── NoteDatabase.java         # Singleton RoomDatabase implementation
│   ├── repository/
│   │   └── NoteRepository.java       # Repository executing async database tasks
│   └── viewmodel/
│       └── NoteViewModel.java        # ViewModel exposing data to Activities
└── res/
    ├── layout/
    │   ├── activity_main.xml         # Main screen with toolbar, recycler, empty state, FAB
    │   ├── activity_add_edit_note.xml# Editor screen with title, content, save button
    │   └── item_note.xml             # MaterialCardView item layout
    ├── menu/
    │   ├── menu_main.xml             # Search and Delete All menu items
    │   └── menu_add_edit.xml         # Save and Delete action buttons
    └── values/
        ├── strings.xml               # Centralized text resources
        ├── colors.xml                # Color definitions
        └── themes.xml                # Material 3 application theme
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK with platform tools installed

### Installation & Run

1. Clone or open the project folder in Android Studio:
   ```bash
   git clone https://github.com/Vizzly-nM09/SimpleNotes.git
   ```
2. Allow Android Studio to sync Gradle dependencies.
3. Connect an Android device or start an Android Virtual Device (AVD).
4. Click **Run** (`Shift + F10`) or build the APK via Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
