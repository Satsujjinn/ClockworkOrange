import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import PauseIcon from '@mui/icons-material/Pause';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import { useState, useEffect } from 'react';
import Switch from '@mui/material/Switch';
import { useMidiOutput } from '../hooks/useMidiOutput';

interface TransportBarProps {
  onPlay: () => void;
  onPause: () => void;
  onRecord: () => void;
  onToggleTheme: () => void;
  dark: boolean;
  bindKeys?: boolean;
}

export const TransportBar = ({ onPlay, onPause, onRecord, onToggleTheme, dark, bindKeys }: TransportBarProps) => {
  const [tempo, setTempo] = useState(120);
  const [volume, setVolume] = useState(0.8);
  const { playNote } = useMidiOutput();

  useEffect(() => {
    if (!bindKeys) return;
    const handler = (e: KeyboardEvent) => {
      if (e.code === 'Space') {
        e.preventDefault();
        import('tone').then(Tone => {
          if (Tone.Transport.state === 'started') {
            onPause();
          } else {
            onPlay();
          }
        });
      } else if (e.code === 'ArrowRight' || e.code === 'ArrowLeft') {
        import('tone').then(Tone => {
          const delta = e.code === 'ArrowRight' ? 1 : -1;
          Tone.Transport.seconds = Math.max(0, Tone.Transport.seconds + delta);
        });
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [bindKeys, onPlay, onPause]);

  return (
    <Stack direction="row" spacing={2} alignItems="center" p={1}>
      <IconButton aria-label="record" onClick={() => { onRecord(); playNote('C4'); }} color="error">
        <FiberManualRecordIcon />
      </IconButton>
      <IconButton aria-label="play" onClick={onPlay} color="primary">
        <PlayArrowIcon />
      </IconButton>
      <IconButton aria-label="pause" onClick={onPause} color="primary">
        <PauseIcon />
      </IconButton>
      <Typography>Tempo</Typography>
      <Slider value={tempo} onChange={(_, v) => setTempo(v as number)} min={60} max={200} sx={{ width: 100 }} />
      <Typography>Volume</Typography>
      <Slider value={volume} onChange={(_, v) => setVolume(v as number)} min={0} max={1} step={0.01} sx={{ width: 100 }} />
      <Switch checked={dark} onChange={onToggleTheme} inputProps={{ 'aria-label': 'toggle theme' }} />
    </Stack>
  );
};
