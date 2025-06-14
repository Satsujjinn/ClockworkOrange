import { useEffect, useRef } from 'react';
import * as Tone from 'tone';

export function useAudioContext() {
  const started = useRef(false);

  useEffect(() => {
    if (!started.current) {
      Tone.start();
      started.current = true;
    }
  }, []);

  return Tone.getContext();
}
