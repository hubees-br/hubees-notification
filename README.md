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
* [`isNotificationClosed()`](#isnotificationclosed)
* [`closeNotification()`](#closenotification)
* [`isExactAlarmPermissionGranted()`](#isexactalarmpermissiongranted)
* [`requestExactAlarmPermission()`](#requestexactalarmpermission)

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

</docgen-api>
