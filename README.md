# LegendAI Music Helper

LegendAI helps generate AI-powered music compositions.

## Setup
1. Obtain an API key from [LegendAI](https://legendai.com) after creating an account.
2. Run the app once to create encrypted preferences, then open the device's `data/data/com.legendai.musichelper/shared_prefs/legend_prefs.xml` and insert your `API_KEY` value.
3. Alternatively, modify `Config.kt` to provide the key directly.

## Build & Run
1. Open the project in Android Studio (Electric Eel or later).
2. Allow Gradle to download dependencies.
3. Connect an Android device or start an emulator.
4. Press **Run**.

## Usage
1. Pick a genre or select a short reference clip.
2. Adjust tempo and key sliders.
3. Tap **Generate Song** to call LegendAI and display progress.
4. Play each generated stem.
5. Tap **Mixdown & Export** to save a merged WAV file under the app's external files directory.

Enjoy experimenting with LegendAI!
