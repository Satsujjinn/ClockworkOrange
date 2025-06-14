import { render, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import { useChordStream } from '../hooks/useChordStream';
import { ChordTimeline } from '../components/ChordTimeline';

jest.mock('react-dnd', () => ({
  useDrag: () => [{}, () => {}],
  useDrop: () => [{}, () => {}]
}));

class MockWebSocket {
  static instances: MockWebSocket[] = [];
  onmessage: ((e: { data: string }) => void) | null = null;
  onclose: (() => void) | null = null;
  onerror: (() => void) | null = null;
  constructor(url: string) {
    MockWebSocket.instances.push(this);
  }
  close() {
    this.onclose && this.onclose();
  }
  sendMessage(msg: any) {
    this.onmessage && this.onmessage({ data: JSON.stringify(msg) });
  }
}

(global as any).WebSocket = MockWebSocket as any;

const Wrapper = () => {
  const { events } = useChordStream();
  return <ChordTimeline events={events} onMove={() => {}} />;
};

test('timeline updates on chord websocket message', async () => {
  const { findByText } = render(<Wrapper />);
  const ws = MockWebSocket.instances[0];
  act(() => {
    ws.sendMessage({ time: 1, chord: 'C' });
  });
  expect(await findByText('C')).toBeInTheDocument();
});
