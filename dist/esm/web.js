import { WebPlugin } from '@capacitor/core';
export class HubeesNotificationWeb extends WebPlugin {
    async sendNotificationBeePass(options) {
        return { success: true };
    }
    async sendNotificationBeeMoving(options) {
        return { success: true };
    }
    async isNotificationClosed() {
        return { notificationClosed: false };
    }
    async closeNotification() {
        return { success: true };
    }
    async isExactAlarmPermissionGranted() {
        return { granted: true };
    }
    async requestExactAlarmPermission() {
        console.log('Simulando o pedido de permissão para alarmes exatos');
    }
    async isIgnoringBatteryOptimization() {
        return { isIgnoring: true };
    }
    async requestIgnoreBatteryOptimization() {
        console.log('Simulando o pedido para ignorar otimizações de bateria');
    }
}
//# sourceMappingURL=web.js.map