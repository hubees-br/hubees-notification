// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "HubeesNotification",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "HubeesNotification",
            targets: ["HubeesNotificationPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "HubeesNotificationPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/HubeesNotificationPlugin"),
        .testTarget(
            name: "HubeesNotificationPluginTests",
            dependencies: ["HubeesNotificationPlugin"],
            path: "ios/Tests/HubeesNotificationPluginTests")
    ]
)