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
                Text("2048")
                    .font(.system(size: 96, weight: .black))
                    .foregroundStyle(LinearGradient(colors: [Color(hex: 0x6DB33F), Color(hex: 0xEFC94A), Color(hex: 0xE8792C), Color(hex: 0x8A46E0)], startPoint: .leading, endPoint: .trailing))
                Text("Fruit Edition").font(.system(size: 34, weight: .heavy)).foregroundColor(Color(hex: 0x2B2B2B))

                Spacer()

                Text(lang.t(.difficulty)).font(.system(size: 13, weight: .bold))
                    .foregroundColor(Theme.primary).frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading, 30).padding(.bottom, 8)

                HStack(spacing: 6) {
                    ForEach(options, id: \.size) { opt in
                        let sel = difficulty == opt.size
                        Button {
                            SoundManager.shared.click()
                            difficulty = opt.size
                            GameStore.shared.difficulty = opt.size
                        } label: {
                            VStack(spacing: 2) {
                                Text(lang.t(opt.label)).font(.system(size: 15, weight: .bold))
                                    .foregroundColor(sel ? .white : Color(hex: 0x1C1B1F))
                                Text("\(opt.size)×\(opt.size)").font(.system(size: 12))
                                    .foregroundColor(sel ? .white.opacity(0.9) : Color(hex: 0x6B6B70))
                            }
                            .frame(maxWidth: .infinity).padding(.vertical, 12)
                            .background(sel ? AnyView(Theme.blueButton) : AnyView(Color.clear))
                            .cornerRadius(15)
                        }
                    }
                }
                .padding(6).background(Color.white).cornerRadius(20).shadow(radius: 6)
                .padding(.horizontal, 24)

                NavigationLink(value: Route.game(size: difficulty, resume: false)) {
                    Text("▶  " + lang.t(.startGame))
                        .font(.system(size: 19, weight: .heavy)).foregroundColor(.white)
                        .frame(maxWidth: .infinity).frame(height: 64)
                        .background(Theme.blueButton).clipShape(Capsule())
                        .shadow(color: Theme.primary.opacity(0.5), radius: 12, y: 4)
                }
                .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })
                .padding(.horizontal, 24).padding(.top, 22)

                NavigationLink(value: Route.drop) {
                    Text("Orchard Drop")
                        .font(.system(size: 17, weight: .heavy)).foregroundColor(.white)
                        .frame(maxWidth: .infinity).frame(height: 56)
                        .background(LinearGradient(colors: [Color(hex: 0x34C77B), Color(hex: 0x179A57)],
                                                   startPoint: .top, endPoint: .bottom))
                        .clipShape(Capsule())
                        .shadow(color: Color(hex: 0x179A57).opacity(0.45), radius: 10, y: 4)
                }
                .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })
                .padding(.horizontal, 24).padding(.top, 12)

                if hasSaved {
                    NavigationLink(value: Route.game(size: GameStore.shared.savedGameSize, resume: true)) {
                        Text(lang.t(.continueGame))
                            .font(.system(size: 16, weight: .bold)).foregroundColor(Theme.primary)
                            .frame(maxWidth: .infinity).frame(height: 50)
                            .background(Color.white).clipShape(Capsule()).shadow(radius: 6)
                    }
                    .simultaneousGesture(TapGesture().onEnded { SoundManager.shared.click() })
                    .padding(.horizontal, 24).padding(.top, 12)
                }

                Spacer()
            }
        }
        .onAppear {
            hasSaved = GameStore.shared.hasSavedGame
            difficulty = GameStore.shared.difficulty
        }
    }
}
