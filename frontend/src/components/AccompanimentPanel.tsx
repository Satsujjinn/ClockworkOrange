import Grid from '@mui/material/Grid';
import Slider from '@mui/material/Slider';
import Typography from '@mui/material/Typography';
import { useState } from 'react';

interface ControlProps {
  label: string;
  value: number;
  onChange: (v: number) => void;
}

const Control = ({ label, value, onChange }: ControlProps) => (
  <Grid item xs={12} sm={4}>
    <Typography gutterBottom>{label}</Typography>
    <Slider value={value} onChange={(_, v) => onChange(v as number)} min={0} max={1} step={0.01} />
  </Grid>
);

export const AccompanimentPanel = () => {
  const [bass, setBass] = useState(0.5);
  const [drums, setDrums] = useState(0.5);
  const [piano, setPiano] = useState(0.5);

  return (
    <Grid container spacing={2} p={2}>
      <Control label="Bass" value={bass} onChange={setBass} />
      <Control label="Drums" value={drums} onChange={setDrums} />
      <Control label="Piano" value={piano} onChange={setPiano} />
    </Grid>
  );
};
