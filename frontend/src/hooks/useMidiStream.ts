import { useEffect } from 'react';
import { useMidiOutput } from './useMidiOutput';

export interface MidiMessage {
  type: string;
  note: string;
  velocity: number;
  duration: number;
}

export function useMidiStream(url: string = 'ws://localhost:8000/ws/midi') {
  const { playNote } = useMidiOutput();

  useEffect(() => {
    const ws = new WebSocket(url);
    ws.onmessage = (evt) => {
      try {
        const msg = JSON.parse(evt.data) as MidiMessage;
        if (msg.type === 'note_on') {
          playNote(msg.note, msg.duration);
        }
      } catch {
        // ignore malformed messages
      }
    };
    return () => {
      ws.close();
    };
  }, [url, playNote]);
}
