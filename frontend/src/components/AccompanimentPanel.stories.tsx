import type { Meta, StoryObj } from '@storybook/react';
import { AccompanimentPanel } from './AccompanimentPanel';

const meta: Meta<typeof AccompanimentPanel> = {
  title: 'Components/AccompanimentPanel',
  component: AccompanimentPanel
};

export default meta;
export type Story = StoryObj<typeof AccompanimentPanel>;

export const Default: Story = {};
