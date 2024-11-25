import { registerPlugin } from '@capacitor/core';
const HubeesNotification = registerPlugin('HubeesNotification', {
    web: () => import('./web').then((m) => new m.HubeesNotificationWeb()),
});
export * from './definitions';
export { HubeesNotification };
//# sourceMappingURL=index.js.map