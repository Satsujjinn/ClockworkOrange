import { useState, useCallback, useEffect } from 'react';

export interface ChordEvent {
  time: number;
  chord: string;
}

export function useChordStream(initial: ChordEvent[] = []) {
  const [events, setEvents] = useState<ChordEvent[]>(initial);
  const [ws, setWs] = useState<WebSocket>();

  const addEvent = useCallback((evt: ChordEvent) => {
    setEvents((prev) => [...prev, evt].sort((a, b) => a.time - b.time));
  }, []);

  useEffect(() => {
    let socket: WebSocket | undefined;
    let timer: ReturnType<typeof setTimeout>;

    const connect = () => {
      socket = new WebSocket('ws://localhost:8000/ws');
      setWs(socket);
      socket.onmessage = (ev) => {
        try {
          const data = JSON.parse(ev.data);
          if (typeof data.time === 'number' && typeof data.chord === 'string') {
            addEvent({ time: data.time, chord: data.chord });
          }
        } catch (err) {
          console.error('bad message', err);
        }
      };
      socket.onclose = () => {
        timer = setTimeout(connect, 1000);
      };
      socket.onerror = () => {
        socket?.close();
      };
    };

    connect();

    return () => {
      clearTimeout(timer);
      socket?.close();
    };
  }, [addEvent]);

  return { events, addEvent };
}
