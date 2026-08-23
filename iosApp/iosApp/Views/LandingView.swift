import SwiftUI

enum Route: Hashable {
    case game(size: Int, resume: Bool)
    case drop
    case settings
}

struct LandingView: View {
    @ObservedObject private var lang = LanguageManager.shared
    @State private var difficulty = GameStore.shared.difficulty
    @State private var hasSaved = GameStore.shared.hasSavedGame

    private let options: [(size: Int, label: L)] = [(5, .easy), (4, .normal), (3, .hard)]

    var body: some View {
        ZStack {
            LinearGradient(colors: [Color(hex: 0xEAF6F2), Color(hex: 0xF3EEFB), Color(hex: 0xFBEEF3)],
                           startPoint: .topLeading, endPoint: .bottomTrailing)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                HStack {
                    Spacer()
                    HStack(spacing: 4) {
                        Text("🏆"); Text("\(GameStore.shared.bestScore)").bold()
                    }
                    .padding(.horizontal, 15).padding(.vertical, 8)
                    .background(Color.white).clipShape(Capsule()).shadow(radius: 6)

                    NavigationLink(value: Route.settings) {
                        Text("⚙️").font(.system(size: 19))
                            .frame(width: 42, height: 42).background(Color.white)
                            .clipShape(Circle()).shadow(radius: 6)
                    }
                }
                .padding(.horizontal, 24).padding(.top, 12)

                Spacer()

                Text("🍉🍓🍌🍇🍑").font(.system(size: 46))
                Text("Tipsy Orchard")
                    .font(.system(size: 54, weight: .black))
                    .foregroundStyle(LinearGradient(colors: [Color(hex: 0x6DB33F), Color(hex: 0xEFC94A), Color(hex: 0xE8792C), Color(hex: 0x8A46E0)], startPoint: .leading, endPoint: .trailing))
                    .lineLimit(1).minimumScaleFactor(0.6)
                    .padding(.horizontal, 16)
                Text(lang.t(.tagline))
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(Color(hex: 0x5B5B64))

                Spacer()

                // Primary game: the falling-and-tilting hybrid.
                NavigationLink(value: Route.drop) {
                    Text("\u{25B6}  " + lang.t(.play))
                        .font(.system(size: 21, weight: .heavy)).foregroundColor(.white)
                        .frame(maxWidth: .infinity).frame(height: 66)
                        .background(LinearGradient(colors: [Color(hex: 0x34C77B), Color(hex: 0x179A57)],
                                                   startPoint: .top, endPoint: .bottom))
                        .clipShape(Capsule())
                        .shadow(color: Color(hex: 0x179A57).opacity(0.5), radius: 12, y: 4)
                }
                .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })
                .padding(.horizontal, 24)

                // Secondary: the classic sliding game, kept as bonus content.
                Text(lang.t(.classicMode))
                    .font(.system(size: 11, weight: .heavy))
                    .foregroundColor(Color(hex: 0x9A9AA2))
                    .padding(.top, 28)
                Text(lang.t(.titleSubtitle))
                    .font(.system(size: 17, weight: .heavy))
                    .foregroundColor(Color(hex: 0x3A3A42))
                    .padding(.top, 2)

                HStack(spacing: 5) {
                    ForEach(options, id: \.size) { opt in
                        let sel = difficulty == opt.size
                        Button {
                            SoundManager.shared.click()
                            difficulty = opt.size
                            GameStore.shared.difficulty = opt.size
                        } label: {
                            Text(lang.t(opt.label) + "  \(opt.size)\u{00D7}\(opt.size)")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(sel ? .white : Color(hex: 0x1C1B1F))
                                .frame(maxWidth: .infinity).padding(.vertical, 9)
                                .background(sel ? AnyView(Theme.blueButton) : AnyView(Color.clear))
                                .cornerRadius(12)
                        }
                    }
                }
                .padding(5).background(Color.white).cornerRadius(16).shadow(radius: 4)
                .padding(.horizontal, 24).padding(.top, 8)

                HStack(spacing: 10) {
                    NavigationLink(value: Route.game(size: difficulty, resume: false)) {
                        Text(lang.t(.startGame))
                            .font(.system(size: 15, weight: .bold)).foregroundColor(Theme.primary)
                            .frame(maxWidth: .infinity).frame(height: 48)
                            .background(Color.white).clipShape(Capsule()).shadow(radius: 5)
                    }
                    .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })

                    if hasSaved {
                        NavigationLink(value: Route.game(size: GameStore.shared.savedGameSize, resume: true)) {
                            Text(lang.t(.continueGame))
                                .font(.system(size: 15, weight: .bold)).foregroundColor(Theme.primary)
                                .frame(maxWidth: .infinity).frame(height: 48)
                                .background(Color.white).clipShape(Capsule()).shadow(radius: 5)
                        }
                        .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })
                    }
                }
                .padding(.horizontal, 24).padding(.top, 8)

                Spacer()
            }
        }
        .onAppear {
            hasSaved = GameStore.shared.hasSavedGame
            difficulty = GameStore.shared.difficulty
        }
    }
}
