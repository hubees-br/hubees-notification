export interface HubeesNotificationPlugin {
  sendNotificationBeePass(options: {
    remainingTime: number;
    permanence: number;
    arrivalTime: string;
  }): Promise<{ success: boolean }>;

  isNotificationClosed(): Promise<{ notificationClosed: boolean }>;

  closeNotification(): Promise<{ success: boolean }>;
}
