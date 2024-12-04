export interface HubeesNotificationPlugin {
  sendNotificationBeePass(options: {
    remainingTime: number;
    permanence: number;
    arrivalTime: string;
  }): Promise<{ success: boolean }>;

  sendNotificationBeeMoving(options: {
    value: number;
    permanence: number;
    arrivalTime: string;
  }): Promise<{ success: boolean }>;

  isNotificationClosed(): Promise<{ notificationClosed: boolean }>;

  closeNotification(): Promise<{ success: boolean }>;

  isExactAlarmPermissionGranted(): Promise<{ granted: boolean }>;

  requestExactAlarmPermission(): Promise<void>;

  isIgnoringBatteryOptimization(): Promise<{ isIgnoring: boolean }>;

  requestIgnoreBatteryOptimization(): Promise<void>;
}
