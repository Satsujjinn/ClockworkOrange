import { render } from '@testing-library/react';
import { WaveformViewer } from '../components/WaveformViewer';
import '@testing-library/jest-dom';

test('renders canvas', () => {
  const { container } = render(<WaveformViewer />);
  expect(container.querySelector('canvas')).toBeInTheDocument();
});
