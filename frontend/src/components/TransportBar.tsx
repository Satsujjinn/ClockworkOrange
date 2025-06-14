import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import PauseIcon from '@mui/icons-material/Pause';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import Tooltip from '@mui/material/Tooltip';
import Zoom from '@mui/material/Zoom';
import { useState } from 'react';
import Switch from '@mui/material/Switch';
import { useMidiOutput } from '../hooks/useMidiOutput';

interface TransportBarProps {
  onPlay: () => void;
  onPause: () => void;
  onRecord: () => void;
  onToggleTheme: () => void;
  dark: boolean;
}

export const TransportBar = ({ onPlay, onPause, onRecord, onToggleTheme, dark }: TransportBarProps) => {
  const [tempo, setTempo] = useState(120);
  const [volume, setVolume] = useState(0.8);
  const { playNote } = useMidiOutput();

  return (
    <Stack direction="row" spacing={2} alignItems="center" p={1}>
      <Tooltip title="Record (R)" TransitionComponent={Zoom} arrow>
        <IconButton aria-label="record" onClick={() => { onRecord(); playNote('C4'); }} color="error">
          <FiberManualRecordIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Play (Space)" TransitionComponent={Zoom} arrow>
        <IconButton aria-label="play" onClick={onPlay} color="primary">
          <PlayArrowIcon />
        </IconButton>
      </Tooltip>
      <Tooltip title="Pause" TransitionComponent={Zoom} arrow>
        <IconButton aria-label="pause" onClick={onPause} color="primary">
          <PauseIcon />
        </IconButton>
      </Tooltip>
      <Typography>Tempo</Typography>
      <Tooltip title={`Tempo: ${tempo}`} TransitionComponent={Zoom} arrow>
        <Slider value={tempo} onChange={(_, v) => setTempo(v as number)} min={60} max={200} sx={{ width: 100, transition: 'background-color 0.3s' }} />
      </Tooltip>
      <Typography>Volume</Typography>
      <Tooltip title={`Volume: ${Math.round(volume * 100)}`} TransitionComponent={Zoom} arrow>
        <Slider value={volume} onChange={(_, v) => setVolume(v as number)} min={0} max={1} step={0.01} sx={{ width: 100, transition: 'background-color 0.3s' }} />
      </Tooltip>
      <Tooltip title={dark ? 'Switch to light theme' : 'Switch to dark theme'} TransitionComponent={Zoom} arrow>
        <Switch checked={dark} onChange={onToggleTheme} inputProps={{ 'aria-label': 'toggle theme' }} />
      </Tooltip>
    </Stack>
  );
};
