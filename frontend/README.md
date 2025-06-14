# Music GUI Frontend

This React frontend provides a music composition interface using Tone.js and the WebMIDI API. Run `npm install` then `npm run dev` to start the Vite dev server. The UI supports dark and light themes, lazy loaded visualizers and MIDI playback via WebMIDI with a Tone.js synth fallback.

- **WaveformViewer** – displays a waveform and frequency bars.
- **ChordTimeline** – virtualized drag-and-drop chord blocks.
- **AccompanimentPanel** – sliders for bass/drums/piano levels.
- **TransportBar** – play/pause/record controls and theme toggle.

The app is designed to connect to the FastAPI backend in this repository for chord suggestions and accompaniment generation.
