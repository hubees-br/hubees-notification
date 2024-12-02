import { WebPlugin } from '@capacitor/core';
import type { HubeesNotificationPlugin } from './definitions';
export declare class HubeesNotificationWeb extends WebPlugin implements HubeesNotificationPlugin {
    sendNotificationBeePass(options: {
        remainingTime: number;
        permanence: number;
        arrivalTime: string;
    }): Promise<{
        success: boolean;
    }>;
    isNotificationClosed(): Promise<{
        notificationClosed: boolean;
    }>;
    closeNotification(): Promise<{
        success: boolean;
    }>;
    isExactAlarmPermissionGranted(): Promise<{
        granted: boolean;
    }>;
    requestExactAlarmPermission(): Promise<void>;
}
