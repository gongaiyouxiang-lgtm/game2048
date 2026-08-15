import UIKit
import ComposeApp

// UIKit lifecycle (not SwiftUI): hosting a ComposeUIViewController inside SwiftUI's
// UIViewControllerRepresentable crashes on iOS 26 in _UIHostingView/AttributeGraph layout.
// Making the Compose view controller the window's root avoids that layer entirely.
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.rootViewController = MainViewControllerKt.MainViewController()
        window.makeKeyAndVisible()
        self.window = window
        return true
    }
}
