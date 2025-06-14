import { useState, useCallback } from 'react';

export interface ChordEvent {
  time: number;
  chord: string;
}

export function useChordStream(initial: ChordEvent[] = []) {
  const [events, setEvents] = useState<ChordEvent[]>(initial);

  const addEvent = useCallback((evt: ChordEvent) => {
    setEvents((prev) => [...prev, evt].sort((a, b) => a.time - b.time));
  }, []);

  return { events, addEvent };
}
