import Foundation

/// A single tile on the board. `id` is stable across moves so SwiftUI can animate it.
struct Tile: Identifiable, Equatable {
    let id = UUID()
    var value: Int
    var row: Int
    var col: Int
    var isNew: Bool = true
    var mergedThisMove: Bool = false
}

/// 2048 game state + rules. Board size is configurable (3, 4 or 5).
final class GameLogic: ObservableObject {
    @Published private(set) var tiles: [Tile] = []
    @Published private(set) var score: Int = 0
    @Published private(set) var bestScore: Int = 0
    @Published private(set) var isGameOver: Bool = false
    @Published private(set) var canUndo: Bool = false
    @Published var reachedGoal: Bool = false

    let size: Int
    private var undoStack: [(tiles: [Tile], score: Int)] = []

    private let store = GameStore.shared

    init(size: Int, resume: Bool) {
        self.size = size
        self.bestScore = store.bestScore
        if resume, let saved = store.loadBoard(size: size) {
            self.tiles = saved.tiles
            self.score = saved.score
            self.isGameOver = Self.checkGameOver(saved.tiles, size: size)
        } else {
            startNew()
        }
    }

    // MARK: - Public actions

    func startNew() {
        undoStack.removeAll()
        canUndo = false
        tiles.removeAll()
        score = 0
        isGameOver = false
        reachedGoal = false
        spawn()
        spawn()
        persist()
    }

    func undo() {
        guard let last = undoStack.popLast() else { return }
        tiles = last.tiles
        score = last.score
        isGameOver = false
        canUndo = !undoStack.isEmpty
        persist()
    }

    /// Returns true if the board changed (a real move happened — a slide OR a merge).
    @discardableResult
    func move(_ dir: Direction) -> Bool {
        let snapshot = tiles
        let snapshotScore = score
        let before = valueLayout(tiles)

        // Clear per-move flags.
        for i in tiles.indices { tiles[i].isNew = false; tiles[i].mergedThisMove = false }

        var grid = makeGrid()
        var gained = 0

        for line in 0..<size {
            let values = extractLine(grid, line: line, dir: dir)
            let (merged, points, _) = Self.collapse(values)
            gained += points
            writeLine(&grid, line: line, dir: dir, tiles: merged)
        }

        let newTiles = grid.compactMap { $0 }
        // A move counts if any cell's value changed position or merged. Comparing the
        // value layout catches pure slides (into empty cells) as well as merges.
        if valueLayout(newTiles) == before { return false }

        undoStack.append((snapshot, snapshotScore))
        if undoStack.count > 20 { undoStack.removeFirst() }
        canUndo = true

        tiles = newTiles
        score += gained
        if score > bestScore { bestScore = score; store.bestScore = score }
        if !reachedGoal && tiles.contains(where: { $0.value >= 2048 }) { reachedGoal = true }

        spawn()
        isGameOver = Self.checkGameOver(tiles, size: size)
        persist()
        return true
    }

    // MARK: - Grid helpers

    /// Row-major grid of optional tiles.
    private func makeGrid() -> [Tile?] {
        var grid = [Tile?](repeating: nil, count: size * size)
        for t in tiles { grid[t.row * size + t.col] = t }
        return grid
    }

    /// Row-major grid of tile VALUES (0 = empty). Used to tell whether a move
    /// actually changed the board (positions or merges), independent of tile ids.
    private func valueLayout(_ tiles: [Tile]) -> [Int] {
        var g = [Int](repeating: 0, count: size * size)
        for t in tiles { g[t.row * size + t.col] = t.value }
        return g
    }

    /// Extract one row/column in the direction of travel (index 0 = destination edge).
    private func extractLine(_ grid: [Tile?], line: Int, dir: Direction) -> [Tile] {
        var result: [Tile] = []
        for i in 0..<size {
            let (r, c): (Int, Int)
            switch dir {
            case .left:  (r, c) = (line, i)
            case .right: (r, c) = (line, size - 1 - i)
            case .up:    (r, c) = (i, line)
            case .down:  (r, c) = (size - 1 - i, line)
            }
            if let t = grid[r * size + c] { result.append(t) }
        }
        return result
    }

    /// Write a collapsed line back into the grid, updating tile row/col.
    private func writeLine(_ grid: inout [Tile?], line: Int, dir: Direction, tiles: [Tile]) {
        // Clear the line first.
        for i in 0..<size {
            let (r, c) = coord(line: line, i: i, dir: dir)
            grid[r * size + c] = nil
        }
        for (i, tileIn) in tiles.enumerated() {
            var t = tileIn
            let (r, c) = coord(line: line, i: i, dir: dir)
            t.row = r; t.col = c
            grid[r * size + c] = t
        }
    }

    private func coord(line: Int, i: Int, dir: Direction) -> (Int, Int) {
        switch dir {
        case .left:  return (line, i)
        case .right: return (line, size - 1 - i)
        case .up:    return (i, line)
        case .down:  return (size - 1 - i, line)
        }
    }

    /// Merge equal neighbours once, packing toward index 0. Returns (tiles, points, changed).
    private static func collapse(_ input: [Tile]) -> ([Tile], Int, Bool) {
        var out: [Tile] = []
        var points = 0
        var i = 0
        while i < input.count {
            if i + 1 < input.count && input[i].value == input[i + 1].value {
                var merged = input[i]
                merged.value *= 2
                merged.mergedThisMove = true
                merged.isNew = false
                out.append(merged)
                points += merged.value
                i += 2
            } else {
                out.append(input[i])
                i += 1
            }
        }
        let changed = out.count != input.count ||
            zip(out, input).contains { $0.value != $1.value }
        return (out, points, changed)
    }

    private func spawn() {
        let occupied = Set(tiles.map { $0.row * size + $0.col })
        let free = (0..<size * size).filter { !occupied.contains($0) }
        guard let cell = free.randomElement() else { return }
        let value = Double.random(in: 0..<1) < 0.9 ? 2 : 4
        tiles.append(Tile(value: value, row: cell / size, col: cell % size, isNew: true))
    }

    private static func checkGameOver(_ tiles: [Tile], size: Int) -> Bool {
        if tiles.count < size * size { return false }
        var grid = [Int](repeating: 0, count: size * size)
        for t in tiles { grid[t.row * size + t.col] = t.value }
        for r in 0..<size {
            for c in 0..<size {
                let v = grid[r * size + c]
                if c + 1 < size && grid[r * size + c + 1] == v { return false }
                if r + 1 < size && grid[(r + 1) * size + c] == v { return false }
            }
        }
        return true
    }

    private func persist() {
        if isGameOver {
            store.clearBoard()
        } else {
            store.saveBoard(size: size, tiles: tiles, score: score)
        }
    }
}

enum Direction { case left, right, up, down }
