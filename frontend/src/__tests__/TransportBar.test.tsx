import { render, fireEvent } from '@testing-library/react';
jest.mock('../hooks/useMidiOutput', () => ({ useMidiOutput: () => ({ playNote: jest.fn() }) }));
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

test('play button displays tooltip on hover', async () => {
  const { getByLabelText, findByRole } = render(
    <TransportBar onPlay={() => {}} onPause={() => {}} onRecord={() => {}} onToggleTheme={() => {}} dark={false} />
  );
  fireEvent.mouseOver(getByLabelText('play'));
  expect(await findByRole('tooltip')).toHaveTextContent('Play (Space)');
});
