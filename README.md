# MusicGen Helper

This app uses the open-source MusicGen model hosted on Hugging Face to generate AI-powered music compositions.

## Setup
1. Obtain a free API token from [Hugging Face](https://huggingface.co/settings/tokens).
2. Run the app once to create encrypted preferences, then open the device's `data/data/com.legendai.musichelper/shared_prefs/legend_prefs.xml` and insert your `API_KEY` value.
3. Alternatively, modify `Config.kt` to provide the token directly.

## Build & Run
1. Open the project in Android Studio (Electric Eel or later).
2. Allow Gradle to download dependencies. If the Gradle wrapper JAR is missing,
   use a locally installed Gradle (`gradle assembleDebug`).
3. Connect an Android device or start an emulator.
4. Press **Run**.

## Usage
1. Pick a genre or select a short reference clip.
2. Adjust tempo and key sliders.
3. Tap **Generate Song** to call MusicGen and display progress.
4. Play the generated preview.
5. Tap **Export** to save the WAV file under the app's external files directory.

Enjoy experimenting with MusicGen!
