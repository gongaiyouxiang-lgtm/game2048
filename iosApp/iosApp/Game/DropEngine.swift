import Foundation

// MARK: - Model

/// One fruit resting in the crate. `id` is stable across moves so SwiftUI animates it
/// rather than cross-fading. Struct copies keep the id, which is what lets a merged
/// fruit animate out of the cell it merged into.
struct FruitCell: Identifiable, Equatable {
    let id = UUID()
    var value: Int
    var row: Int
    var col: Int
    var justMerged: Bool = false
}

/// The crate: a fixed grid, row 0 at the top, gravity pulling toward the last row.
/// Deliberately not square (2048 is 4x4) and not a tall well (Tetris is 10x20).
struct Crate: Equatable {
    static let cols = 6
    static let rows = 9

    var cells: [FruitCell?]

    init() {
        cells = Array(repeating: nil, count: Crate.rows * Crate.cols)
    }

    subscript(row: Int, col: Int) -> FruitCell? {
        get { cells[row * Crate.cols + col] }
        set { cells[row * Crate.cols + col] = newValue }
    }

    static func inBounds(row: Int, col: Int) -> Bool {
        row >= 0 && row < rows && col >= 0 && col < cols
    }

    /// Values by position, ignoring ids — used to tell whether a tilt actually changed anything.
    var valueLayout: [Int] {
        cells.map { $0?.value ?? 0 }
    }

    var fruits: [FruitCell] {
        cells.compactMap { $0 }
    }
}

/// One cell of a falling piece, as an offset from the piece's pivot.
struct PieceCell: Equatable {
    var dr: Int
    var dc: Int
    var value: Int
}

/// A falling piece: a small cluster of fruit that the player positions and rotates.
struct Piece: Equatable {
    var cells: [PieceCell]
    var row: Int
    var col: Int

    /// Absolute board positions of every cell in the piece.
    var placed: [(row: Int, col: Int, value: Int)] {
        cells.map { (row + $0.dr, col + $0.dc, $0.value) }
    }

    func moved(dr: Int, dc: Int) -> Piece {
        var p = self
        p.row += dr
        p.col += dc
        return p
    }

    /// Rotate clockwise about the pivot: (dr, dc) -> (dc, -dr).
    func rotated() -> Piece {
        var p = self
        p.cells = cells.map { PieceCell(dr: $0.dc, dc: -$0.dr, value: $0.value) }
        return p
    }
}

// MARK: - Pure rules

/// All board rules as pure functions on value types, so they can be reasoned about
/// and tuned without touching any view code.
enum DropRules {

    /// Highest fruit in the chain. Reaching it triggers a harvest.
    static let harvestValue = 2048

    // MARK: Placement

    static func canPlace(_ piece: Piece, in crate: Crate) -> Bool {
        for cell in piece.placed {
            guard Crate.inBounds(row: cell.row, col: cell.col) else { return false }
            if crate[cell.row, cell.col] != nil { return false }
        }
        return true
    }

    /// Stamp a piece into the crate as loose fruit.
    static func lock(_ piece: Piece, into crate: Crate) -> Crate {
        var out = crate
        for cell in piece.placed where Crate.inBounds(row: cell.row, col: cell.col) {
            out[cell.row, cell.col] = FruitCell(value: cell.value, row: cell.row, col: cell.col)
        }
        return out
    }

    // MARK: Merging

    /// One merge pass. Scans bottom-to-top, left-to-right; a fruit merges with the one to
    /// its right or the one directly above it, and the result lands in the lower / left-most
    /// cell so merges settle downward. Every fruit merges at most once per pass.
    static func mergePass(_ crate: Crate) -> (crate: Crate, gained: Int, merged: Bool) {
        var out = crate
        var consumed = Set<Int>()
        var gained = 0
        var didMerge = false

        var r = Crate.rows - 1
        while r >= 0 {
            for c in 0..<Crate.cols {
                let i = r * Crate.cols + c
                guard let cell = out.cells[i], !consumed.contains(i) else { continue }

                // Merge with the neighbour to the right.
                if c + 1 < Crate.cols {
                    let j = r * Crate.cols + (c + 1)
                    if let other = out.cells[j], other.value == cell.value, !consumed.contains(j) {
                        var merged = cell
                        merged.value *= 2
                        merged.justMerged = true
                        out.cells[i] = merged
                        out.cells[j] = nil
                        consumed.insert(i)
                        gained += merged.value
                        didMerge = true
                        continue
                    }
                }

                // Merge with the neighbour above.
                if r - 1 >= 0 {
                    let j = (r - 1) * Crate.cols + c
                    if let other = out.cells[j], other.value == cell.value, !consumed.contains(j) {
                        var merged = cell
                        merged.value *= 2
                        merged.justMerged = true
                        out.cells[i] = merged
                        out.cells[j] = nil
                        consumed.insert(i)
                        gained += merged.value
                        didMerge = true
                    }
                }
            }
            r -= 1
        }
        return (out, gained, didMerge)
    }

    // MARK: Gravity

    /// Settle every column toward the floor.
    static func gravity(_ crate: Crate) -> (crate: Crate, moved: Bool) {
        var out = Crate()
        var moved = false

        for c in 0..<Crate.cols {
            var write = Crate.rows - 1
            var r = Crate.rows - 1
            while r >= 0 {
                if var cell = crate[r, c] {
                    if cell.row != write { moved = true }
                    cell.row = write
                    cell.col = c
                    out[write, c] = cell
                    write -= 1
                }
                r -= 1
            }
        }
        return (out, moved)
    }

    // MARK: Tilt — the 2048 half

    /// Tip the whole crate sideways: each row packs against that edge, merging identical
    /// neighbours as it packs. This is a 2048 row collapse applied to a falling-fruit board.
    static func tilt(_ crate: Crate, left: Bool) -> (crate: Crate, gained: Int, changed: Bool) {
        var out = Crate()
        var gained = 0

        for r in 0..<Crate.rows {
            // Gather the row in travel order (nearest the destination edge first).
            var line: [FruitCell] = []
            if left {
                for c in 0..<Crate.cols {
                    if let cell = crate[r, c] { line.append(cell) }
                }
            } else {
                var c = Crate.cols - 1
                while c >= 0 {
                    if let cell = crate[r, c] { line.append(cell) }
                    c -= 1
                }
            }

            // Pack, merging equal neighbours once each.
            var packed: [FruitCell] = []
            var i = 0
            while i < line.count {
                if i + 1 < line.count && line[i].value == line[i + 1].value {
                    var merged = line[i]
                    merged.value *= 2
                    merged.justMerged = true
                    packed.append(merged)
                    gained += merged.value
                    i += 2
                } else {
                    packed.append(line[i])
                    i += 1
                }
            }

            for (k, original) in packed.enumerated() {
                var cell = original
                let c = left ? k : (Crate.cols - 1 - k)
                cell.row = r
                cell.col = c
                out[r, c] = cell
            }
        }

        return (out, gained, out.valueLayout != crate.valueLayout)
    }

    // MARK: Harvest

    /// A fruit that reaches the top of the chain pops, clearing its eight neighbours.
    /// This is the goal and the pressure valve when the crate clogs up.
    static func harvestPass(_ crate: Crate) -> (crate: Crate, gained: Int, harvested: Bool) {
        guard let idx = crate.cells.firstIndex(where: { ($0?.value ?? 0) >= harvestValue }) else {
            return (crate, 0, false)
        }
        var out = crate
        let r = idx / Crate.cols
        let c = idx % Crate.cols

        var gained = (out.cells[idx]?.value ?? 0) * 2
        out.cells[idx] = nil

        for dr in -1...1 {
            for dc in -1...1 {
                let nr = r + dr, nc = c + dc
                guard Crate.inBounds(row: nr, col: nc) else { continue }
                if let neighbour = out[nr, nc] {
                    gained += neighbour.value
                    out[nr, nc] = nil
                }
            }
        }
        return (out, gained, true)
    }

    // MARK: Cascade

    /// Result of settling the board completely after a lock or a tilt.
    struct Resolution {
        var crate: Crate
        var gained: Int
        var steps: Int        // cascade depth — each merge round is one step
        var harvests: Int
    }

    /// Merge, settle, repeat until nothing changes. Each round multiplies its score by the
    /// cascade depth, so long chains pay far more than the same merges made separately.
    static func resolve(_ crate: Crate) -> Resolution {
        var cleared = crate
        for i in cleared.cells.indices {
            cleared.cells[i]?.justMerged = false
        }
        var board = gravity(cleared).crate
        var total = 0
        var steps = 0
        var harvests = 0

        while true {
            let merge = mergePass(board)
            if merge.merged {
                steps += 1
                total += merge.gained * steps
                board = gravity(merge.crate).crate
                continue
            }
            let harvest = harvestPass(board)
            if harvest.harvested {
                harvests += 1
                total += harvest.gained
                board = gravity(harvest.crate).crate
                continue
            }
            break
        }
        return Resolution(crate: board, gained: total, steps: steps, harvests: harvests)
    }

    // MARK: Spawning

    /// Fruit values that can drop in. Weighted low, drifting up as the score grows so the
    /// crate fills faster later on.
    static func spawnValue(score: Int) -> Int {
        let roll = Int.random(in: 0..<100)
        if score > 4000 {
            if roll < 45 { return 2 }
            if roll < 80 { return 4 }
            return 8
        } else if score > 1200 {
            if roll < 60 { return 2 }
            if roll < 92 { return 4 }
            return 8
        } else {
            return roll < 75 ? 2 : 4
        }
    }

    /// A new piece at the top of the crate: usually a pair, sometimes a single, rarely a line of three.
    static func newPiece(score: Int) -> Piece {
        let roll = Int.random(in: 0..<100)
        var cells: [PieceCell] = []

        if roll < 15 {
            cells = [PieceCell(dr: 0, dc: 0, value: spawnValue(score: score))]
        } else if roll < 92 {
            cells = [
                PieceCell(dr: 0, dc: 0, value: spawnValue(score: score)),
                PieceCell(dr: 0, dc: 1, value: spawnValue(score: score)),
            ]
        } else {
            cells = [
                PieceCell(dr: 0, dc: 0, value: spawnValue(score: score)),
                PieceCell(dr: 0, dc: 1, value: spawnValue(score: score)),
                PieceCell(dr: 0, dc: 2, value: spawnValue(score: score)),
            ]
        }
        return Piece(cells: cells, row: 0, col: 2)
    }
}

// MARK: - Engine

/// Observable wrapper around `DropRules`. Owns the falling piece, the tilt economy and
/// the game-over condition; all board mathematics lives in the pure rules above.
final class DropEngine: ObservableObject {

    @Published private(set) var crate = Crate()
    @Published private(set) var piece: Piece?
    @Published private(set) var nextPiece: Piece
    @Published private(set) var score = 0
    @Published private(set) var bestScore = 0
    @Published private(set) var tiltCharges = DropEngine.startingCharges
    @Published private(set) var lastCascade = 0
    @Published private(set) var isGameOver = false

    static let startingCharges = 3
    static let maxCharges = 5
    /// A cascade this deep or deeper earns another tilt.
    static let cascadeForCharge = 3

    private let bestKey = "drop_best_score"

    init() {
        nextPiece = DropRules.newPiece(score: 0)
        bestScore = UserDefaults.standard.integer(forKey: bestKey)
        startNew()
    }

    // MARK: Lifecycle

    func startNew() {
        crate = Crate()
        score = 0
        tiltCharges = DropEngine.startingCharges
        lastCascade = 0
        isGameOver = false
        nextPiece = DropRules.newPiece(score: 0)
        spawn()
    }

    private func spawn() {
        var candidate = nextPiece
        candidate.row = 0
        candidate.col = 2
        nextPiece = DropRules.newPiece(score: score)

        guard DropRules.canPlace(candidate, in: crate) else {
            piece = nil
            isGameOver = true
            recordBest()
            SoundManager.shared.gameOver()
            return
        }
        piece = candidate
    }

    private func recordBest() {
        if score > bestScore {
            bestScore = score
            UserDefaults.standard.set(score, forKey: bestKey)
        }
    }

    // MARK: Player input

    func moveLeft() { shift(dc: -1) }
    func moveRight() { shift(dc: 1) }

    private func shift(dc: Int) {
        guard let current = piece, !isGameOver else { return }
        let candidate = current.moved(dr: 0, dc: dc)
        if DropRules.canPlace(candidate, in: crate) { piece = candidate }
    }

    func rotate() {
        guard let current = piece, !isGameOver else { return }
        let candidate = current.rotated()
        if DropRules.canPlace(candidate, in: crate) {
            piece = candidate
            return
        }
        // Nudge sideways if the rotation is only blocked by a wall.
        for dc in [-1, 1, -2, 2] {
            let kicked = candidate.moved(dr: 0, dc: dc)
            if DropRules.canPlace(kicked, in: crate) {
                piece = kicked
                return
            }
        }
    }

    /// One gravity tick for the falling piece. Locks the piece when it can fall no further.
    func tick() {
        guard let current = piece, !isGameOver else { return }
        let candidate = current.moved(dr: 1, dc: 0)
        if DropRules.canPlace(candidate, in: crate) {
            piece = candidate
        } else {
            settle(current)
        }
    }

    func hardDrop() {
        guard var current = piece, !isGameOver else { return }
        while DropRules.canPlace(current.moved(dr: 1, dc: 0), in: crate) {
            current = current.moved(dr: 1, dc: 0)
        }
        piece = current
        settle(current)
    }

    /// Tip the crate. Costs one charge and can set off very long chains — which is why
    /// charges are rationed rather than free every turn.
    func tilt(left: Bool) {
        guard !isGameOver, tiltCharges > 0, piece != nil else { return }
        let tilted = DropRules.tilt(crate, left: left)
        guard tilted.changed else { return }

        tiltCharges -= 1
        SoundManager.shared.slide()

        let resolution = DropRules.resolve(tilted.crate)
        crate = resolution.crate
        reseatFallingPiece()
        score += tilted.gained + resolution.gained
        lastCascade = resolution.steps
        awardCharges(for: resolution)
        if resolution.steps > 0 {
            SoundManager.shared.merge()
            SoundManager.shared.hapticMerge()
        }
        recordBest()
    }

    // MARK: Resolution

    private func settle(_ current: Piece) {
        piece = nil
        let locked = DropRules.lock(current, into: crate)
        let resolution = DropRules.resolve(locked)

        crate = resolution.crate
        score += resolution.gained
        lastCascade = resolution.steps
        awardCharges(for: resolution)

        if resolution.steps > 0 {
            SoundManager.shared.merge()
            SoundManager.shared.hapticMerge()
        } else {
            SoundManager.shared.slide()
        }
        if resolution.harvests > 0 { SoundManager.shared.win() }
        recordBest()
        spawn()
    }

    /// After a tilt the stack can rise above the piece still in mid-air. Lift the piece
    /// to the nearest row where it fits; if there is none, the crate is full.
    private func reseatFallingPiece() {
        guard var candidate = piece else { return }
        if DropRules.canPlace(candidate, in: crate) { return }
        while candidate.row > 0 {
            candidate = candidate.moved(dr: -1, dc: 0)
            if DropRules.canPlace(candidate, in: crate) {
                piece = candidate
                return
            }
        }
        piece = nil
        isGameOver = true
        recordBest()
        SoundManager.shared.gameOver()
    }

    private func awardCharges(for resolution: DropRules.Resolution) {
        var earned = 0
        if resolution.steps >= DropEngine.cascadeForCharge { earned += 1 }
        earned += resolution.harvests
        if earned > 0 {
            tiltCharges = min(DropEngine.maxCharges, tiltCharges + earned)
        }
    }
}
