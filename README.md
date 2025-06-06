# MusicGen Helper

This app uses the open-source MusicGen model hosted on Hugging Face to generate AI-powered music compositions.

## Setup
1. Obtain a free API token from [Hugging Face](https://huggingface.co/settings/tokens).
2. Launch the app and open **Settings** from the menu to enter your API token. It will be stored securely in encrypted preferences.
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
   prompts to trust the wrapper and allow dependencies to download. If you run
   from the command line and `gradle/wrapper/gradle-wrapper.jar` is missing,
   bootstrap the wrapper by running the following from the project root (a local
   Gradle install is required):
   ```bash
   gradle wrapper
   ```
   After the jar is generated you can invoke tasks with `./gradlew`. Some
   projects commit the jar so first-time users don't need Gradle installed—check
   your repository policy before doing so.
5. Provide your MusicGen API key (see **Setup** above).
6. Connect an Android device or start an emulator.
7. Press **Run** or execute `./gradlew assembleDebug` from the command line.

## Testing
1. Make sure an Android SDK is installed and `local.properties` specifies its
   location via `sdk.dir` as described in **Build & Run**.
2. If `gradle/wrapper/gradle-wrapper.jar` is missing, bootstrap the wrapper:
   ```bash
   gradle wrapper
   ```
   Then you can invoke the tests with the wrapper.
3. Execute the unit tests:
   ```bash
   ./gradlew test
   ```

## Usage
1. Pick a genre or select a short reference clip.
2. Adjust tempo, key, and duration sliders.
3. Tap **Generate Song** to call MusicGen and display progress.
4. Play the generated preview.
5. Tap **Export** to save the WAV file under the app's external files directory.
   The export uses an app-specific folder so no storage permission is needed.
6. Review the suggested chord progression displayed before generating to help
   kick-start your arrangement.

Enjoy experimenting with MusicGen!
