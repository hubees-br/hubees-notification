import { registerPlugin } from '@capacitor/core';
import type { HubeesNotificationPlugin } from './definitions';

const HubeesNotification = registerPlugin<HubeesNotificationPlugin>('HubeesNotification', {
  web: () => import('./web').then((m) => new m.HubeesNotificationWeb()),
});

export * from './definitions';
export { HubeesNotification };
