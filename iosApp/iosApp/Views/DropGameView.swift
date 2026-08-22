import SwiftUI
import Combine

/// The hybrid mode: fruit falls and stacks, identical fruit merges up the chain, and
/// tipping the whole crate sideways is a rationed special move.
struct DropGameView: View {
    @StateObject private var engine = DropEngine()
    @ObservedObject private var lang = LanguageManager.shared
    @Environment(\.dismiss) private var dismiss

    @State private var showGameOver = false
    @State private var dragAccum: CGFloat = 0
    @State private var tickCount = 0

    private let timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()

    /// Timer ticks between gravity steps — the crate fills faster as the score climbs.
    private var stepsPerFall: Int {
        max(3, 9 - engine.score / 1500)
    }

    var body: some View {
        ZStack {
            Theme.gameGradient.ignoresSafeArea()

            VStack(spacing: 10) {
                header
                hud
                board
                tiltControls
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 14)
            .padding(.top, 6)
        }
        .navigationBarBackButtonHidden(true)
        .overlay { if showGameOver { gameOverDialog } }
        .onReceive(timer) { _ in
            guard !engine.isGameOver else { return }
            tickCount += 1
            if tickCount >= stepsPerFall {
                tickCount = 0
                engine.tick()
            }
        }
        .onChange(of: engine.isGameOver) { _, over in
            withAnimation { showGameOver = over }
        }
    }

    // MARK: Header

    private var header: some View {
        HStack {
            Button {
                SoundManager.shared.click(); dismiss()
            } label: {
                Text("←").font(.system(size: 22, weight: .bold)).foregroundColor(.white)
            }
            Spacer()
            Text("Orchard Drop")
                .font(.system(size: 20, weight: .heavy))
                .foregroundColor(.white)
            Spacer()
            Button {
                SoundManager.shared.click(); engine.startNew(); showGameOver = false
            } label: {
                Image(systemName: "arrow.clockwise")
                    .font(.system(size: 18, weight: .bold)).foregroundColor(.white)
            }
        }
    }

    // MARK: HUD

    private var hud: some View {
        HStack(spacing: 8) {
            statBox(title: lang.t(.currentScore), value: "\(engine.score)")
            statBox(title: lang.t(.bestScore), value: "\(engine.bestScore)")
            nextBox
        }
    }

    private func statBox(title: String, value: String) -> some View {
        VStack(spacing: 2) {
            Text(title)
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(Color(hex: 0x7C3AED))
                .lineLimit(1)
            Text(value)
                .font(.system(size: 16, weight: .heavy))
                .foregroundColor(Color(hex: 0x2A2A2A))
        }
        .padding(.vertical, 8)
        .frame(maxWidth: .infinity)
        .background(Color.white.opacity(0.9))
        .cornerRadius(12)
    }

    private var nextBox: some View {
        VStack(spacing: 3) {
            Text("NEXT")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(Color(hex: 0x7C3AED))
            HStack(spacing: 2) {
                ForEach(engine.nextPiece.cells.indices, id: \.self) { i in
                    Text(Theme.style(for: engine.nextPiece.cells[i].value).icon)
                        .font(.system(size: 15))
                }
            }
        }
        .padding(.vertical, 8)
        .frame(maxWidth: .infinity)
        .background(Color.white.opacity(0.9))
        .cornerRadius(12)
    }

    // MARK: Board

    private var board: some View {
        GeometryReader { geo in
            let pad: CGFloat = 3
            let cell = (geo.size.width - pad * CGFloat(Crate.cols + 1)) / CGFloat(Crate.cols)
            let height = cell * CGFloat(Crate.rows) + pad * CGFloat(Crate.rows + 1)

            ZStack(alignment: .topLeading) {
                RoundedRectangle(cornerRadius: 14)
                    .fill(Theme.boardPurple)
                    .frame(width: geo.size.width, height: height)

                ForEach(0..<Crate.rows, id: \.self) { r in
                    ForEach(0..<Crate.cols, id: \.self) { c in
                        RoundedRectangle(cornerRadius: 8)
                            .fill(Theme.boardCell)
                            .frame(width: cell, height: cell)
                            .offset(x: pos(c, pad, cell), y: pos(r, pad, cell))
                    }
                }

                // Fruit resting in the crate.
                ForEach(engine.crate.fruits) { fruit in
                    fruitTile(value: fruit.value, cell: cell, popped: fruit.justMerged)
                        .offset(x: pos(fruit.col, pad, cell), y: pos(fruit.row, pad, cell))
                }

                // The falling piece.
                if let piece = engine.piece {
                    let placed = piece.placed
                    ForEach(placed.indices, id: \.self) { i in
                        if placed[i].row >= 0 {
                            fruitTile(value: placed[i].value, cell: cell, popped: false)
                                .offset(x: pos(placed[i].col, pad, cell),
                                        y: pos(placed[i].row, pad, cell))
                        }
                    }
                }
            }
            .frame(width: geo.size.width, height: height)
            .contentShape(Rectangle())
            .onTapGesture { engine.rotate() }
            .gesture(dragGesture(cell: cell))
            .animation(.easeOut(duration: 0.12), value: engine.crate.valueLayout)
        }
        .aspectRatio(CGFloat(Crate.cols) / CGFloat(Crate.rows), contentMode: .fit)
    }

    private func fruitTile(value: Int, cell: CGFloat, popped: Bool) -> some View {
        let style = Theme.style(for: value)
        return VStack(spacing: 0) {
            Text("\(value)")
                .font(.system(size: cell * 0.24, weight: .bold))
                .foregroundColor(Color(hex: 0x2A2A2A))
                .frame(maxWidth: .infinity, alignment: .leading)
            Text(style.icon)
                .font(.system(size: cell * 0.42))
        }
        .padding(cell * 0.08)
        .frame(width: cell, height: cell)
        .background(style.fill)
        .cornerRadius(8)
        .overlay(RoundedRectangle(cornerRadius: 8).stroke(style.border, lineWidth: 2))
        .scaleEffect(popped ? 1.1 : 1)
    }

    private func pos(_ index: Int, _ pad: CGFloat, _ cell: CGFloat) -> CGFloat {
        pad * CGFloat(index + 1) + cell * CGFloat(index)
    }

    /// Drag sideways to steer the piece a column at a time; flick down to slam it home.
    private func dragGesture(cell: CGFloat) -> some Gesture {
        DragGesture(minimumDistance: 8)
            .onChanged { value in
                let step = cell + 3
                while value.translation.width - dragAccum > step {
                    engine.moveRight(); dragAccum += step
                }
                while value.translation.width - dragAccum < -step {
                    engine.moveLeft(); dragAccum -= step
                }
            }
            .onEnded { value in
                if value.translation.height > 50 && abs(value.translation.width) < 50 {
                    engine.hardDrop()
                }
                dragAccum = 0
            }
    }

    // MARK: Tilt

    private var tiltControls: some View {
        VStack(spacing: 6) {
            HStack(spacing: 5) {
                Text("TILT")
                    .font(.system(size: 11, weight: .heavy))
                    .foregroundColor(.white.opacity(0.9))
                ForEach(0..<DropEngine.maxCharges, id: \.self) { i in
                    Circle()
                        .fill(i < engine.tiltCharges ? Color(hex: 0xFFC93C) : Color.white.opacity(0.25))
                        .frame(width: 9, height: 9)
                }
                if engine.lastCascade > 1 {
                    Text("CHAIN x\(engine.lastCascade)")
                        .font(.system(size: 12, weight: .heavy))
                        .foregroundColor(Color(hex: 0xFFE066))
                        .padding(.leading, 6)
                }
            }

            HStack(spacing: 10) {
                tiltButton(left: true)
                tiltButton(left: false)
            }
        }
    }

    private func tiltButton(left: Bool) -> some View {
        Button {
            engine.tilt(left: left)
        } label: {
            Image(systemName: left ? "arrow.left.to.line" : "arrow.right.to.line")
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(Color(hex: 0x6B4E00))
                .frame(maxWidth: .infinity)
                .frame(height: 46)
                .background(Color(hex: 0xFFC93C).opacity(engine.tiltCharges > 0 ? 1 : 0.4))
                .cornerRadius(14)
        }
        .disabled(engine.tiltCharges == 0)
    }

    // MARK: Game over

    private var gameOverDialog: some View {
        ZStack {
            Color.black.opacity(0.45).ignoresSafeArea()
            VStack(spacing: 14) {
                Text(lang.t(.gameOver)).font(.title2).bold()
                Text(String(format: lang.t(.finalScore), engine.score)).font(.body)
                Button {
                    SoundManager.shared.click(); showGameOver = false; engine.startNew()
                } label: {
                    Label(lang.t(.newGame), systemImage: "arrow.clockwise")
                        .bold().foregroundColor(.white)
                        .padding(.horizontal, 24).padding(.vertical, 12)
                        .background(Theme.primary).cornerRadius(12)
                }
            }
            .padding(24)
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .padding(40)
        }
    }
}
