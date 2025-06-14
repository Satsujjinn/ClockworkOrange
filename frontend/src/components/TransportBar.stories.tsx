import type { Meta, StoryObj } from '@storybook/react';
import { TransportBar } from './TransportBar';

const meta: Meta<typeof TransportBar> = {
  title: 'Components/TransportBar',
  component: TransportBar
};

export default meta;
export type Story = StoryObj<typeof TransportBar>;

export const Default: Story = {
  args: {
    onPlay: () => console.log('play'),
    onPause: () => console.log('pause'),
    onRecord: () => console.log('record'),
    onToggleTheme: () => console.log('toggle'),
    dark: false
  }
};
