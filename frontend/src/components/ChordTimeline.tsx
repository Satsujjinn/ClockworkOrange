import { useRef, CSSProperties } from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import { useDrag, useDrop } from 'react-dnd';
import { FixedSizeList as List } from 'react-window';
import { ChordEvent } from '../hooks/useChordStream';

export interface ChordTimelineProps {
  events: ChordEvent[];
  onMove: (index: number, time: number) => void;
}

export const ChordTimeline = ({ events, onMove }: ChordTimelineProps) => {
  const [, drop] = useDrop({ accept: 'CHORD' });

  const Row = ({ index, style }: { index: number; style: CSSProperties }) => {
    const evt = events[index];
    return (
      <ChordBlock
        style={style}
        index={index}
        time={evt.time}
        chord={evt.chord}
        onMove={onMove}
      />
    );
  };

  return (
    <Box ref={drop} sx={{ height: 100 }}>
      <List
        height={100}
        width={800}
        itemCount={events.length}
        itemSize={90}
        layout="horizontal"
        overscanCount={5}
        style={{ overflowX: 'auto' }}
      >
        {Row}
      </List>
    </Box>
  );
};

interface BlockProps {
  index: number;
  time: number;
  chord: string;
  onMove: (index: number, time: number) => void;
  style: CSSProperties;
}

const ChordBlock = ({ index, time, chord, onMove, style }: BlockProps) => {
  const [{ isDragging }, drag] = useDrag({
    type: 'CHORD',
    item: { index },
    collect: (monitor) => ({ isDragging: monitor.isDragging() })
  });
  const ref = useRef<HTMLDivElement>(null);
  return (
    <Paper
      ref={drag}
      style={style}
      sx={{
        width: 80,
        height: 40,
        m: 1,
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
