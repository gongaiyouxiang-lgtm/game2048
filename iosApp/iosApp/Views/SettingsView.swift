import SwiftUI

struct SettingsView: View {
    @ObservedObject private var lang = LanguageManager.shared
    @Environment(\.dismiss) private var dismiss
    @State private var soundOn = GameStore.shared.soundOn
    @State private var vibrationOn = GameStore.shared.vibrationOn

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Button { dismiss() } label: {
                        Text("←").font(.system(size: 22, weight: .bold)).foregroundColor(.primary)
                    }
                    Text(lang.t(.settings)).font(.title2).bold().padding(.leading, 4)
                }
                .padding(.bottom, 8)

                section(lang.t(.audioHaptics))
                VStack(spacing: 0) {
                    toggleRow("🔊", lang.t(.sound), $soundOn) { GameStore.shared.soundOn = $0 }
                    Divider().padding(.leading, 44)
                    toggleRow("📳", lang.t(.vibration), $vibrationOn) { GameStore.shared.vibrationOn = $0 }
                }
                .background(Color(.secondarySystemBackground)).cornerRadius(14)

                section(lang.t(.appearance))
                Text(lang.t(.language)).font(.system(size: 13)).foregroundColor(.secondary).padding(.leading, 4).padding(.bottom, 8)
                HStack(spacing: 10) {
                    ForEach(AppLanguage.allCases) { l in
                        let sel = lang.language == l
                        Button { lang.language = l } label: {
                            Text(l.display).font(.system(size: 14, weight: .semibold))
                                .foregroundColor(sel ? .white : .primary)
                                .frame(maxWidth: .infinity).padding(.vertical, 12)
                                .background(sel ? Theme.primary : Color(.secondarySystemBackground))
                                .cornerRadius(12)
                        }
                    }
                }

                section(lang.t(.about))
                HStack {
                    Text("ℹ️").padding(.trailing, 8)
                    Text(lang.t(.version))
                    Spacer()
                    Text(appVersion).foregroundColor(.secondary)
                }
                .padding(16).background(Color(.secondarySystemBackground)).cornerRadius(14)
            }
            .padding(20)
        }
        .navigationBarBackButtonHidden(true)
    }

    private func section(_ text: String) -> some View {
        Text(text).font(.system(size: 12, weight: .bold)).foregroundColor(Theme.primary)
            .kerning(0.6).padding(.top, 22).padding(.bottom, 8).padding(.leading, 4)
    }

    private func toggleRow(_ icon: String, _ label: String, _ value: Binding<Bool>, onChange: @escaping (Bool) -> Void) -> some View {
        HStack {
            Text(icon).padding(.trailing, 12)
            Text(label)
            Spacer()
            Toggle("", isOn: value).labelsHidden()
                .onChange(of: value.wrappedValue) { _, v in onChange(v) }
        }
        .padding(.horizontal, 16).padding(.vertical, 8)
    }

    private var appVersion: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "1.0"
    }
}
