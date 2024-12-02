import { WebPlugin } from '@capacitor/core';
export class HubeesNotificationWeb extends WebPlugin {
    async sendNotificationBeePass(options) {
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
}
//# sourceMappingURL=web.js.map