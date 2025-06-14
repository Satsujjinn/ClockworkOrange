import { useRef } from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import { useDrag, useDrop } from 'react-dnd';
import { ChordEvent } from '../hooks/useChordStream';

export interface ChordTimelineProps {
  events: ChordEvent[];
  onMove: (index: number, time: number) => void;
}

export const ChordTimeline = ({ events, onMove }: ChordTimelineProps) => {
  const [, drop] = useDrop({ accept: 'CHORD' });
  return (
    <Box ref={drop} sx={{ height: 100, position: 'relative', overflow: 'auto' }}>
      {events.map((evt, idx) => (
        <ChordBlock key={idx} index={idx} time={evt.time} chord={evt.chord} onMove={onMove} />
      ))}
    </Box>
  );
};

interface BlockProps {
  index: number;
  time: number;
  chord: string;
  onMove: (index: number, time: number) => void;
}

const ChordBlock = ({ index, time, chord, onMove }: BlockProps) => {
  const [{ isDragging }, drag] = useDrag({
    type: 'CHORD',
    item: { index },
    collect: (monitor) => ({ isDragging: monitor.isDragging() })
  });
  const ref = useRef<HTMLDivElement>(null);
  return (
    <Paper
      ref={drag}
      sx={{
        width: 80,
        height: 40,
        position: 'absolute',
        left: time * 80,
        top: 20,
        opacity: isDragging ? 0.5 : 1,
        textAlign: 'center',
        lineHeight: '40px',
        cursor: 'move'
      }}
    >
      {chord}
    </Paper>
  );
};
