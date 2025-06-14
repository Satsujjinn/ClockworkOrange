import { render, fireEvent, waitFor } from '@testing-library/react';
jest.mock('../hooks/useMidiOutput', () => ({ useMidiOutput: () => ({ playNote: jest.fn() }) }));
jest.mock('tone', () => ({ Transport: { state: 'stopped', seconds: 0 } }));
import { TransportBar } from '../components/TransportBar';
import '@testing-library/jest-dom';

test('theme toggle switch calls handler', () => {
  const handler = jest.fn();
  const { getByRole } = render(
    <TransportBar onPlay={() => {}} onPause={() => {}} onRecord={() => {}} onToggleTheme={handler} dark={false} />
  );
  fireEvent.click(getByRole('checkbox'));
  expect(handler).toHaveBeenCalled();
});

test('space toggles play and pause', async () => {
  const onPlay = jest.fn();
  const onPause = jest.fn();
  const { Transport } = await import('tone');
  (Transport as any).state = 'stopped';
  render(
    <TransportBar onPlay={onPlay} onPause={onPause} onRecord={() => {}} onToggleTheme={() => {}} dark={false} bindKeys />
  );
  fireEvent.keyDown(window, { code: 'Space' });
  await waitFor(() => expect(onPlay).toHaveBeenCalled());
  (Transport as any).state = 'started';
  fireEvent.keyDown(window, { code: 'Space' });
  await waitFor(() => expect(onPause).toHaveBeenCalled());
});

test('arrow keys step transport time', async () => {
  const { Transport } = await import('tone');
  (Transport as any).seconds = 0;
  render(
    <TransportBar onPlay={() => {}} onPause={() => {}} onRecord={() => {}} onToggleTheme={() => {}} dark={false} bindKeys />
  );
  fireEvent.keyDown(window, { code: 'ArrowRight' });
  await waitFor(() => expect((Transport as any).seconds).toBe(1));
  fireEvent.keyDown(window, { code: 'ArrowLeft' });
  await waitFor(() => expect((Transport as any).seconds).toBe(0));
});
