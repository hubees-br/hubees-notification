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
}
//# sourceMappingURL=web.js.map