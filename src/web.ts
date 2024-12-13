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

  async sendNotificationBeeMoving(options: {
    value: number;
    permanence: number;
    valueWithDiscount: string;
  }): Promise<{ success: boolean }> {
    return { success: true };
  }

  async isNotificationClosed(): Promise<{ notificationClosed: boolean }> {
    return { notificationClosed: false };
  }

  async closeNotification(): Promise<{ success: boolean }> {
    return { success: true };
  }

  async isExactAlarmPermissionGranted(): Promise<{ granted: boolean }> {
    return { granted: true };
  }

  async requestExactAlarmPermission(): Promise<void> {
    console.log('Simulando o pedido de permissão para alarmes exatos');
  }

  async isIgnoringBatteryOptimization(): Promise<{ isIgnoring: boolean }> {
    return { isIgnoring: true };
  }

  async requestIgnoreBatteryOptimization(): Promise<void> {
    console.log('Simulando o pedido para ignorar otimizações de bateria');
  }
}
