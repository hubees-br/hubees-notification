import Foundation

@objc public class HubeesNotification: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
