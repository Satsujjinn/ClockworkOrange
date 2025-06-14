import type { Meta, StoryObj } from '@storybook/react';
import { ChordTimeline } from './ChordTimeline';
import { ChordEvent } from '../hooks/useChordStream';

const events: ChordEvent[] = [
  { time: 0, chord: 'Cmaj7' },
  { time: 2, chord: 'Fmaj7' },
  { time: 4, chord: 'G7' }
];

const meta: Meta<typeof ChordTimeline> = {
  title: 'Components/ChordTimeline',
  component: ChordTimeline
};

export default meta;
export type Story = StoryObj<typeof ChordTimeline>;

export const Default: Story = {
  args: {
    events,
    onMove: (i, t) => console.log('move', i, t)
  }
};
