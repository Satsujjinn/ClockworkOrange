import { useEffect, useState } from 'react';
import * as Tone from 'tone';
import { WebMidi, Output } from 'webmidi';

export function useMidiOutput() {
  const [output, setOutput] = useState<Output | Tone.Synth | null>(null);

  useEffect(() => {
    WebMidi.enable()
      .then(() => {
        if (WebMidi.outputs.length > 0) {
          setOutput(WebMidi.outputs[0]);
        } else {
          setOutput(new Tone.Synth().toDestination());
        }
      })
      .catch(() => {
        setOutput(new Tone.Synth().toDestination());
      });
  }, []);

  const playNote = (note: string, duration: number = 500) => {
    if (!output) return;
    if ('playNote' in output) {
      (output as Output).playNote(note, { duration });
    } else {
      (output as Tone.Synth).triggerAttackRelease(note, duration / 1000);
    }
  };

  return { playNote };
}
