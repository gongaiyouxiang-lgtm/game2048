import SwiftUI

@main
struct FruitApp: App {
    init() {
        // Allow game sounds even with the silent switch off is not needed; keep default session.
    }

    var body: some Scene {
        WindowGroup {
            NavigationStack {
                LandingView()
                    .navigationDestination(for: Route.self) { route in
                        switch route {
                        case let .game(size, resume):
                            GameView(size: size, resume: resume)
                        case .drop:
                            DropGameView()
                        case .settings:
                            SettingsView()
                        }
                    }
            }
        }
    }
}
