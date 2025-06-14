import { useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import { Suspense, lazy } from 'react';
const WaveformViewer = lazy(() => import('./components/WaveformViewer'));
import { ChordTimeline } from './components/ChordTimeline';
import { AccompanimentPanel } from './components/AccompanimentPanel';
import { TransportBar } from './components/TransportBar';
import { useChordStream } from './hooks/useChordStream';
import { useAudioContext } from './hooks/useAudioContext';

export const App = () => {
  const [dark, setDark] = useState(true);
  const theme = createTheme({ palette: { mode: dark ? 'dark' : 'light' } });
  const { events, addEvent } = useChordStream();
  useAudioContext();

  const handlePlay = () => {
    import('tone').then(Tone => {
      Tone.Transport.start();
    });
  };

  const handlePause = () => {
    import('tone').then(Tone => {
      Tone.Transport.pause();
    });
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box display="flex" height="100vh">
        <Box width={300} p={2} bgcolor="background.paper">
          <Suspense fallback={null}>
            <WaveformViewer />
          </Suspense>
        </Box>
        <Box flex={1} p={2}>
          <ChordTimeline events={events} onMove={() => {}} />
        </Box>
        <Box width={300} p={2} bgcolor="background.paper">
          <AccompanimentPanel />
        </Box>
      </Box>
      <Box position="fixed" bottom={0} width="100%" bgcolor="background.paper">
        <TransportBar
          onPlay={handlePlay}
          onPause={handlePause}
          onRecord={() => {}}
          onToggleTheme={() => setDark(!dark)}
          dark={dark}
          bindKeys
        />
      </Box>
    </ThemeProvider>
  );
};
export default App;
