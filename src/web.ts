import { WebPlugin } from '@capacitor/core';
import type { HubeesNotificationPlugin } from './definitions';

export class HubeesNotificationWeb extends WebPlugin implements HubeesNotificationPlugin {
  async sendNotificationBeePass(options: {
    remainingTime: number;
    permanence: number;
    arrivalTime: string;
  }): Promise<{ success: boolean }> {
    return { success: true };
  }

  async isNotificationClosed(): Promise<{ notificationClosed: boolean }> {
    return { notificationClosed: false };
  }

  async closeNotification(): Promise<{ success: boolean }> {
    return { success: true };
  }
}
