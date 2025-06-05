# MusicGen Helper

This app uses the open-source MusicGen model hosted on Hugging Face to generate AI-powered music compositions.

## Setup
1. Obtain a free API token from [Hugging Face](https://huggingface.co/settings/tokens).
2. Run the app once to create encrypted preferences, then open the device's `data/data/com.legendai.musichelper/shared_prefs/legend_prefs.xml` and insert your `API_KEY` value.
3. Alternatively, modify `Config.kt` to provide the token directly.
4. Ensure the Android SDK path is configured. Copy `local.properties.example` to `local.properties` and update `sdk.dir` to point to your SDK installation.

## Build & Run
1. Clone the repository and enter the project directory:
   ```bash
   git clone <repo-url>
   cd ClockworkOrange
   ```
2. Copy `local.properties.example` to `local.properties` and set `sdk.dir` to
   your Android SDK location. The file is ignored by Git, so commit it only if
   you want to share the SDK path (e.g. for CI).
3. Open the project in Android Studio (Hedgehog or newer) by selecting
   `settings.gradle` when prompted.
4. Android Studio will sync using the Gradle wrapper. The wrapper will
   automatically download `gradle-wrapper.jar` on first run, so accept any
   prompts to trust the wrapper and allow dependencies to download.
5. Provide your MusicGen API key (see **Setup** above).
6. Connect an Android device or start an emulator.
7. Press **Run** or execute `./gradlew assembleDebug` from the command line.

## Usage
1. Pick a genre or select a short reference clip.
2. Adjust tempo and key sliders.
3. Tap **Generate Song** to call MusicGen and display progress.
4. Play the generated preview.
5. Tap **Export** to save the WAV file under the app's external files directory.
   The export uses an app-specific folder so no storage permission is needed.
6. Review the suggested chord progression displayed before generating to help
   kick-start your arrangement.

Enjoy experimenting with MusicGen!
