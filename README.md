# BugIt - Bug Tracking Android Application

Android bug tracking app built with Kotlin and Jetpack Compose.

## Setup Instructions

1. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the BugIt folder and select it

2. **Configure API Keys**
   - Open `app/src/main/java/com/momen/bugit/network/ApiConfig.kt`
   - Replace the API keys with your own:
     ```kotlin
     object ApiConfig {
         const val GOOGLE_SHEETS_API_KEY = "YOUR_GOOGLE_SHEETS_API_KEY"
         const val GOOGLE_SHEETS_SPREADSHEET_ID = "YOUR_SPREADSHEET_ID"
         const val IMAGEBB_API_KEY = "YOUR_IMAGEBB_API_KEY"
     }
     ```

3. **Add Service Account JSON**
   - Create a Google Service Account and download the JSON file
   - Rename it to `service_account.json`
   - Place it in `app/src/main/res/raw/service_account.json`

4. **Build and Run**
   - Sync the project with Gradle files
   - Build the project (Build → Make Project)
   - Run on device or emulator

## Google Sheets Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google Sheets API
4. Create credentials (API Key and Service Account)
5. Share your spreadsheet with the service account email

## ImageBB Setup

1. Go to [ImageBB](https://imgbb.com/)
2. Create an account
3. Get your API key from the dashboard
4. Add it to `ApiConfig.kt`
