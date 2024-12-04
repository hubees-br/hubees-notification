# hubees-notification

Notification

## Install

```bash
npm install hubees-notification
npx cap sync
```

## API

<docgen-index>

* [`sendNotificationBeePass(...)`](#sendnotificationbeepass)
* [`sendNotificationBeeMoving(...)`](#sendnotificationbeemoving)
* [`isNotificationClosed()`](#isnotificationclosed)
* [`closeNotification()`](#closenotification)
* [`isExactAlarmPermissionGranted()`](#isexactalarmpermissiongranted)
* [`requestExactAlarmPermission()`](#requestexactalarmpermission)
* [`isIgnoringBatteryOptimization()`](#isignoringbatteryoptimization)
* [`requestIgnoreBatteryOptimization()`](#requestignorebatteryoptimization)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### sendNotificationBeePass(...)

```typescript
sendNotificationBeePass(options: { remainingTime: number; permanence: number; arrivalTime: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                                                             |
| ------------- | -------------------------------------------------------------------------------- |
| **`options`** | <code>{ remainingTime: number; permanence: number; arrivalTime: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### sendNotificationBeeMoving(...)

```typescript
sendNotificationBeeMoving(options: { value: number; permanence: number; arrivalTime: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                                                     |
| ------------- | ------------------------------------------------------------------------ |
| **`options`** | <code>{ value: number; permanence: number; arrivalTime: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### isNotificationClosed()

```typescript
isNotificationClosed() => Promise<{ notificationClosed: boolean; }>
```

**Returns:** <code>Promise&lt;{ notificationClosed: boolean; }&gt;</code>

--------------------


### closeNotification()

```typescript
closeNotification() => Promise<{ success: boolean; }>
```

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### isExactAlarmPermissionGranted()

```typescript
isExactAlarmPermissionGranted() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### requestExactAlarmPermission()

```typescript
requestExactAlarmPermission() => Promise<void>
```

--------------------


### isIgnoringBatteryOptimization()

```typescript
isIgnoringBatteryOptimization() => Promise<{ isIgnoring: boolean; }>
```

**Returns:** <code>Promise&lt;{ isIgnoring: boolean; }&gt;</code>

--------------------


### requestIgnoreBatteryOptimization()

```typescript
requestIgnoreBatteryOptimization() => Promise<void>
```

--------------------

</docgen-api>
