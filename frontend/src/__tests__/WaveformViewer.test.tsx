import { render } from '@testing-library/react';
import { WaveformViewer } from '../components/WaveformViewer';

test('renders canvas', () => {
  const { container } = render(<WaveformViewer />);
  expect(container.querySelector('canvas')).toBeInTheDocument();
});
