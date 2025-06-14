import type { Meta, StoryObj } from '@storybook/react';
import { WaveformViewer } from './WaveformViewer';

const meta: Meta<typeof WaveformViewer> = {
  title: 'Components/WaveformViewer',
  component: WaveformViewer
};

export default meta;
export type Story = StoryObj<typeof WaveformViewer>;

export const Default: Story = {
  args: {}
};
