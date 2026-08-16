import AVFoundation
import UIKit

/// Plays the bundled game sound effects and merge haptics. Gated on the stored settings.
final class SoundManager {
    static let shared = SoundManager()

    private var players: [String: AVAudioPlayer] = [:]
    private let names = ["merge", "slide", "gameover", "win", "click"]

    private init() {
        for name in names {
            if let url = Bundle.main.url(forResource: name, withExtension: "mp3"),
               let player = try? AVAudioPlayer(contentsOf: url) {
                player.prepareToPlay()
                players[name] = player
            }
        }
    }

    private func play(_ name: String) {
        guard GameStore.shared.soundOn, let player = players[name] else { return }
        player.currentTime = 0
        player.play()
    }

    func merge()    { play("merge") }
    func slide()    { play("slide") }
    func gameOver() { play("gameover") }
    func win()      { play("win") }
    func click()    { play("click") }

    func hapticMerge() {
        guard GameStore.shared.vibrationOn else { return }
        let generator = UIImpactFeedbackGenerator(style: .light)
        generator.prepare()
        generator.impactOccurred()
    }
}
