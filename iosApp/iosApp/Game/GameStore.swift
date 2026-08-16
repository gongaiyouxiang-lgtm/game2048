import Foundation

/// Simple UserDefaults-backed persistence: best score, in-progress board, and settings.
final class GameStore {
    static let shared = GameStore()
    private let defaults = UserDefaults.standard

    private enum Key {
        static let best = "best_score"
        static let boardValues = "board_values"
        static let boardSize = "board_size"
        static let boardScore = "board_score"
        static let difficulty = "difficulty_size"
        static let sound = "sound_on"
        static let vibration = "vibration_on"
    }

    var bestScore: Int {
        get { defaults.integer(forKey: Key.best) }
        set { defaults.set(newValue, forKey: Key.best) }
    }

    /// Selected difficulty as a board size (default 4).
    var difficulty: Int {
        get { let v = defaults.integer(forKey: Key.difficulty); return v == 0 ? 4 : v }
        set { defaults.set(newValue, forKey: Key.difficulty) }
    }

    var soundOn: Bool {
        get { defaults.object(forKey: Key.sound) as? Bool ?? true }
        set { defaults.set(newValue, forKey: Key.sound) }
    }

    var vibrationOn: Bool {
        get { defaults.object(forKey: Key.vibration) as? Bool ?? true }
        set { defaults.set(newValue, forKey: Key.vibration) }
    }

    var hasSavedGame: Bool {
        (defaults.array(forKey: Key.boardValues) as? [Int])?.isEmpty == false
    }

    var savedGameSize: Int {
        let v = defaults.integer(forKey: Key.boardSize); return v == 0 ? 4 : v
    }

    /// Board stored as a flat [value] array of length size*size (0 = empty).
    func saveBoard(size: Int, tiles: [Tile], score: Int) {
        var flat = [Int](repeating: 0, count: size * size)
        for t in tiles { flat[t.row * size + t.col] = t.value }
        defaults.set(flat, forKey: Key.boardValues)
        defaults.set(size, forKey: Key.boardSize)
        defaults.set(score, forKey: Key.boardScore)
    }

    func loadBoard(size: Int) -> (tiles: [Tile], score: Int)? {
        guard let flat = defaults.array(forKey: Key.boardValues) as? [Int],
              flat.count == size * size, flat.contains(where: { $0 > 0 }) else { return nil }
        var tiles: [Tile] = []
        for (i, v) in flat.enumerated() where v > 0 {
            tiles.append(Tile(value: v, row: i / size, col: i % size, isNew: false))
        }
        return (tiles, defaults.integer(forKey: Key.boardScore))
    }

    func clearBoard() {
        defaults.removeObject(forKey: Key.boardValues)
        defaults.removeObject(forKey: Key.boardScore)
    }
}
