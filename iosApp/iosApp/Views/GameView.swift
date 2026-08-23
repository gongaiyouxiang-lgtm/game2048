import SwiftUI

struct GameView: View {
    @StateObject private var game: GameLogic
    @ObservedObject private var lang = LanguageManager.shared
    @Environment(\.dismiss) private var dismiss
    @State private var showGameOver = false

    init(size: Int, resume: Bool) {
        _game = StateObject(wrappedValue: GameLogic(size: size, resume: resume))
    }

    var body: some View {
        ZStack {
            Theme.gameGradient.ignoresSafeArea()
            FloatingFruits()

            VStack(spacing: 6) {
                card
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 12)
            .padding(.top, 6)
        }
        .navigationBarBackButtonHidden(true)
        .overlay { if showGameOver { gameOverDialog } }
        .onChange(of: game.isGameOver) { _, over in
            if over { SoundManager.shared.gameOver() }
            withAnimation { showGameOver = over }
        }
    }

    private var card: some View {
        VStack(spacing: 10) {
            HStack {
                Button {
                    SoundManager.shared.click(); dismiss()
                } label: {
                    Text("←").font(.system(size: 22, weight: .bold)).foregroundColor(Theme.primary)
                }
                Spacer()
            }

            HStack(alignment: .firstTextBaseline, spacing: 6) {
                Text(lang.t(.titleSubtitle)).font(.system(size: 28, weight: .heavy)).foregroundColor(Theme.primary)
                Spacer()
            }

            HStack(spacing: 10) {
                scoreBox(title: lang.t(.bestScore), value: game.bestScore)
                scoreBox(title: lang.t(.currentScore), value: game.score)
            }

            HStack(spacing: 10) {
                Button { SoundManager.shared.click(); game.startNew() } label: {
                    Label(lang.t(.newGame), systemImage: "arrow.clockwise")
                        .font(.system(size: 15, weight: .bold)).foregroundColor(.white)
                        .frame(maxWidth: .infinity).frame(height: 48)
                        .background(Color(hex: 0xFF9F43)).cornerRadius(14)
                }
                Button { SoundManager.shared.click(); game.undo() } label: {
                    Image(systemName: "arrow.uturn.backward")
                        .font(.system(size: 20, weight: .bold)).foregroundColor(Color(hex: 0x6B4E00))
                        .frame(width: 50, height: 50)
                        .background(Color(hex: 0xFFC93C).opacity(game.canUndo ? 1 : 0.5)).cornerRadius(14)
                }
                .disabled(!game.canUndo)
            }

            BoardView(game: game)
        }
        .padding(14)
        .background(Color.white.opacity(0.55))
        .cornerRadius(28)
        .shadow(color: .black.opacity(0.15), radius: 8, y: 4)
    }

    private func scoreBox(title: String, value: Int) -> some View {
        VStack(spacing: 2) {
            Text(title).font(.system(size: 15, weight: .bold)).foregroundColor(Color(hex: 0x7C3AED))
            Text("\(value)").font(.system(size: 14, weight: .bold)).foregroundColor(Color(hex: 0x2A2A2A))
        }
        .padding(12).frame(maxWidth: .infinity)
        .background(Color.white).cornerRadius(14)
        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color(hex: 0x7C3AED).opacity(0.35), lineWidth: 1.5))
    }

    private var gameOverDialog: some View {
        ZStack {
            Color.black.opacity(0.4).ignoresSafeArea()
            VStack(spacing: 14) {
                Text(lang.t(.gameOver)).font(.title2).bold()
                Text(String(format: lang.t(.finalScore), game.score)).font(.body)
                Button { SoundManager.shared.click(); showGameOver = false; game.startNew() } label: {
                    Label(lang.t(.newGame), systemImage: "arrow.clockwise")
                        .bold().foregroundColor(.white)
                        .padding(.horizontal, 24).padding(.vertical, 12)
                        .background(Theme.primary).cornerRadius(12)
                }
            }
            .padding(24).background(Color(.systemBackground)).cornerRadius(16)
            .padding(40)
        }
    }
}

/// The purple board: background cells + animated fruit tiles, with swipe handling.
private struct BoardView: View {
    @ObservedObject var game: GameLogic

    var body: some View {
        GeometryReader { geo in
            let side = min(geo.size.width, geo.size.height)
            let padding = side * 0.02
            let cell = (side - padding * CGFloat(game.size + 1)) / CGFloat(game.size)

            ZStack(alignment: .topLeading) {
                RoundedRectangle(cornerRadius: 12).fill(Theme.boardPurple)
                    .frame(width: side, height: side)

                ForEach(0..<game.size, id: \.self) { r in
                    ForEach(0..<game.size, id: \.self) { c in
                        RoundedRectangle(cornerRadius: 10).fill(Theme.boardCell)
                            .frame(width: cell, height: cell)
                            .offset(x: pos(c, padding, cell), y: pos(r, padding, cell))
                    }
                }

                ForEach(game.tiles) { tile in
                    TileView(tile: tile, cell: cell)
                        .offset(x: pos(tile.col, padding, cell), y: pos(tile.row, padding, cell))
                }
            }
            .frame(width: side, height: side)
            .contentShape(Rectangle())
            .gesture(swipe)
            .animation(.easeInOut(duration: 0.15), value: game.tiles.map { "\($0.id)\($0.row)\($0.col)" })
        }
        .aspectRatio(1, contentMode: .fit)
    }

    private func pos(_ index: Int, _ padding: CGFloat, _ cell: CGFloat) -> CGFloat {
        padding * CGFloat(index + 1) + cell * CGFloat(index)
    }

    private var swipe: some Gesture {
        DragGesture(minimumDistance: 20)
            .onEnded { v in
                let dx = v.translation.width, dy = v.translation.height
                let dir: Direction = abs(dx) > abs(dy)
                    ? (dx > 0 ? .right : .left)
                    : (dy > 0 ? .down : .up)
                let moved = game.move(dir)
                if moved {
                    if game.tiles.contains(where: { $0.mergedThisMove }) {
                        SoundManager.shared.merge(); SoundManager.shared.hapticMerge()
                    } else {
                        SoundManager.shared.slide()
                    }
                }
            }
    }
}

private struct TileView: View {
    let tile: Tile
    let cell: CGFloat
    @State private var appear = false

    var body: some View {
        let style = Theme.style(for: tile.value)
        VStack(spacing: 0) {
            Text("\(tile.value)").font(.system(size: cell * 0.22, weight: .bold)).foregroundColor(Color(hex: 0x2A2A2A))
                .frame(maxWidth: .infinity, alignment: .leading)
            Text(style.icon).font(.system(size: cell * 0.4))
        }
        .padding(cell * 0.1)
        .frame(width: cell, height: cell)
        .background(style.fill)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(style.border, lineWidth: 2))
        .scaleEffect(appear ? 1 : (tile.isNew ? 0.2 : 1))
        .onAppear { withAnimation(.spring(response: 0.25, dampingFraction: 0.6)) { appear = true } }
    }
}

/// Decorative fruit emojis floating behind the board.
private struct FloatingFruits: View {
    private let items: [(String, CGFloat, CGFloat, CGFloat)] = [
        ("🍓", 0.04, 0.05, 34), ("🍌", 0.90, 0.06, 34), ("🍇", 0.02, 0.30, 34),
        ("🍊", 0.92, 0.26, 30), ("🍑", 0.03, 0.56, 34), ("🍉", 0.90, 0.55, 34),
        ("🍓", 0.10, 0.88, 30), ("🍇", 0.46, 0.9, 34), ("🍌", 0.85, 0.88, 34),
        ("🍊", 0.28, 0.92, 30),
    ]
    @State private var phase = false

    var body: some View {
        GeometryReader { geo in
            ForEach(0..<items.count, id: \.self) { i in
                let it = items[i]
                Text(it.0).font(.system(size: it.3))
                    .position(x: geo.size.width * it.1, y: geo.size.height * it.2 + (phase ? -7 : 7))
                    .animation(.easeInOut(duration: 2.6).repeatForever(autoreverses: true).delay(Double(i) * 0.1), value: phase)
            }
        }
        .ignoresSafeArea()
        .onAppear { phase = true }
    }
}
