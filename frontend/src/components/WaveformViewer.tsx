import { useEffect, useRef } from 'react';
import Box from '@mui/material/Box';

export interface WaveformViewerProps {
  audioBuffer?: AudioBuffer;
  markers?: Array<{ time: number; label?: string }>;
}

export const WaveformViewer = ({ audioBuffer, markers = [] }: WaveformViewerProps) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    if (!canvasRef.current || !audioBuffer) return;
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    const { width, height } = canvas;
    ctx.clearRect(0, 0, width, height);
    // simple waveform render
    const data = audioBuffer.getChannelData(0);
    ctx.beginPath();
    for (let x = 0; x < width; x++) {
      const i = Math.floor((x / width) * data.length);
      const y = (0.5 + data[i] / 2) * height;
      ctx.lineTo(x, y);
    }
    ctx.strokeStyle = 'cyan';
    ctx.stroke();
    markers.forEach(m => {
      const x = (m.time / audioBuffer.duration) * width;
      ctx.fillStyle = 'yellow';
      ctx.fillRect(x, 0, 2, height);
    });
  }, [audioBuffer, markers]);

  return <Box sx={{ p: 1 }}><canvas ref={canvasRef} width={400} height={80} /></Box>;
};
