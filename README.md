# FreePlay Hub 🎮

FreePlay Hub is an Android application built with Kotlin that serves as a platform for discovering free-to-play games using the FreeToGame API. The project follows the MVVM architecture and implements a Dark Glassmorphism design theme.

## 📺 Demo Video

You can watch the application walkthrough here: [Demo Video](YOUR_GOOGLE_DRIVE_LINK_HERE)

## Features

- **Authentication:** Supports Firebase Authentication including Google Sign-In and Email/Password flows.
- **Game Discovery:** Home screen organized into sections: Header, Trending (randomized per session), and All Games.
- **Detailed View:** Provides comprehensive information for each game, including high-resolution banners and game descriptions.
- **Local Persistence:** Uses Room Database for saving "Favorite" games and local caching of the game list.
- **Search:** Dedicated search functionality to find games by title or genre.
- **Offline Support:** Local caching mechanism allows the app to display data during network unavailability.
- **Loading States:** Implements Skeleton UI placeholders during data fetching.

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI Components:** ViewBinding, Navigation Component (SafeArgs), ConstraintLayout, Material 3
- **Networking:** Retrofit & GSON
- **Database:** Room
- **Image Loading:** Glide
- **Backend:** Firebase Authentication

## Setup Instructions

To run this project locally, a Firebase project configuration is required.

### 1. Clone the repository
```bash
git clone <repository-url>
```

### 2. Firebase Configuration
The `google-services.json` file is excluded for security. To set up your own:
1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Register an Android App with the package name: `com.example.freeplayhub`.
3. Download the `google-services.json` and place it in the `app/` directory.
4. Enable **Authentication** in the Firebase Console:
   - Enable **Email/Password** provider.
   - Enable **Google** provider (requires SHA-1 fingerprint).

### 3. SHA-1 Fingerprint
To enable Google Sign-In:
1. Run `./gradlew signingReport` in the Android Studio terminal.
2. Copy the SHA-1 from the `debug` variant and add it to your Firebase project settings.

### 4. Build and Run
1. Perform a Gradle Sync.
2. Deploy the app to an emulator or physical device.

---
