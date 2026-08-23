import SwiftUI

enum AppLanguage: String, CaseIterable, Identifiable {
    case en, zhTW = "zh-TW", zhCN = "zh-CN"
    var id: String { rawValue }
    var display: String {
        switch self {
        case .en: return "English"
        case .zhTW: return "繁體中文"
        case .zhCN: return "简体中文"
        }
    }
}

/// Runtime language switch (no app restart needed — SwiftUI re-renders on change).
final class LanguageManager: ObservableObject {
    static let shared = LanguageManager()
    @Published var language: AppLanguage {
        didSet { UserDefaults.standard.set(language.rawValue, forKey: "app_language") }
    }
    private init() {
        let saved = UserDefaults.standard.string(forKey: "app_language")
        language = AppLanguage(rawValue: saved ?? "") ?? .en
    }

    func t(_ key: L) -> String { L.table[key]?[language] ?? key.rawValue }
}

/// String keys with per-language values.
enum L: String {
    case titleSubtitle, bestScore, currentScore, newGame, undo, gameOver, finalScore
    case congratulation, newBestScore, close
    case startGame, continueGame, back, settings, sound, vibration
    case about, version, audioHaptics, appearance, language
    case difficulty, easy, normal, hard
    case tagline, play, classicMode

    static let table: [L: [AppLanguage: String]] = [
        .titleSubtitle:  [.en: "Classic Slide", .zhTW: "經典滑動", .zhCN: "经典滑动"],
        .tagline:        [.en: "Tilt · Drop · Merge", .zhTW: "傾斜 · 掉落 · 合併", .zhCN: "倾斜 · 掉落 · 合并"],
        .play:           [.en: "Play", .zhTW: "開始遊戲", .zhCN: "开始游戏"],
        .classicMode:    [.en: "ALSO INCLUDED", .zhTW: "另附模式", .zhCN: "另附模式"],
        .bestScore:      [.en: "Best Score", .zhTW: "最高分", .zhCN: "最高分"],
        .currentScore:   [.en: "Current Score", .zhTW: "目前分數", .zhCN: "当前分数"],
        .newGame:        [.en: "New Game", .zhTW: "新遊戲", .zhCN: "新游戏"],
        .undo:           [.en: "Undo", .zhTW: "復原", .zhCN: "撤销"],
        .gameOver:       [.en: "Game Over!", .zhTW: "遊戲結束！", .zhCN: "游戏结束！"],
        .finalScore:     [.en: "Final score: %d", .zhTW: "最終分數：%d", .zhCN: "最终分数：%d"],
        .congratulation: [.en: "Congratulation!!!", .zhTW: "恭喜！！！", .zhCN: "恭喜！！！"],
        .newBestScore:   [.en: "New Best Score: %d", .zhTW: "新紀錄：%d", .zhCN: "新纪录：%d"],
        .close:          [.en: "Close", .zhTW: "關閉", .zhCN: "关闭"],
        .startGame:      [.en: "Start Game", .zhTW: "開始遊戲", .zhCN: "开始游戏"],
        .continueGame:   [.en: "Continue", .zhTW: "繼續遊戲", .zhCN: "继续游戏"],
        .back:           [.en: "Back", .zhTW: "返回", .zhCN: "返回"],
        .settings:       [.en: "Settings", .zhTW: "設定", .zhCN: "设置"],
        .sound:          [.en: "Sound", .zhTW: "音效", .zhCN: "音效"],
        .vibration:      [.en: "Vibration", .zhTW: "震動", .zhCN: "震动"],
        .about:          [.en: "About", .zhTW: "關於", .zhCN: "关于"],
        .version:        [.en: "Version", .zhTW: "版本", .zhCN: "版本"],
        .audioHaptics:   [.en: "Audio & Haptics", .zhTW: "音效與震動", .zhCN: "音效与震动"],
        .appearance:     [.en: "Appearance", .zhTW: "外觀", .zhCN: "外观"],
        .language:       [.en: "Language", .zhTW: "語言", .zhCN: "语言"],
        .difficulty:     [.en: "Difficulty", .zhTW: "難度", .zhCN: "难度"],
        .easy:           [.en: "Easy", .zhTW: "簡單", .zhCN: "简单"],
        .normal:         [.en: "Normal", .zhTW: "普通", .zhCN: "普通"],
        .hard:           [.en: "Hard", .zhTW: "困難", .zhCN: "困难"],
    ]
}
