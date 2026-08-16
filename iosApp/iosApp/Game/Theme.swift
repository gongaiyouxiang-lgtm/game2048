import SwiftUI

extension Color {
    init(hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255,
            opacity: 1
        )
    }
}

/// Visual style for a tile value: fruit emoji + fill + border. Mirrors the Android design.
struct TileStyle {
    let icon: String
    let fill: Color
    let border: Color
}

enum Theme {
    static let primary = Color(hex: 0x176FE7)

    // Game screen gradient (top -> bottom) and board colours.
    static let gameGradient = LinearGradient(
        colors: [Color(hex: 0x5EB1F7), Color(hex: 0x8C93F0), Color(hex: 0xB07CF0)],
        startPoint: .top, endPoint: .bottom
    )
    static let boardPurple = Color(hex: 0x8B5CF6)
    static let boardCell = Color(hex: 0xA78BFA)

    // Landing blue button gradient.
    static let blueButton = LinearGradient(
        colors: [Color(hex: 0x5AA0FF), Color(hex: 0x1466DA)],
        startPoint: .top, endPoint: .bottom
    )

    private static let map: [Int: TileStyle] = [
        2:    TileStyle(icon: "🍉", fill: Color(hex: 0xB5E5BB), border: Color(hex: 0x6BCB77)),
        4:    TileStyle(icon: "🍓", fill: Color(hex: 0xFFBCDF), border: Color(hex: 0xFF7ABE)),
        8:    TileStyle(icon: "🍌", fill: Color(hex: 0xF3E56C), border: Color(hex: 0xEFD54A)),
        16:   TileStyle(icon: "🍊", fill: Color(hex: 0xF6BF9E), border: Color(hex: 0xED803D)),
        32:   TileStyle(icon: "🍇", fill: Color(hex: 0xCBA6FF), border: Color(hex: 0x974DFF)),
        64:   TileStyle(icon: "🥭", fill: Color(hex: 0xFFDB80), border: Color(hex: 0xFFB600)),
        128:  TileStyle(icon: "🥝", fill: Color(hex: 0xDBE4A6), border: Color(hex: 0xB8C94D)),
        256:  TileStyle(icon: "🫐", fill: Color(hex: 0xC5E2FF), border: Color(hex: 0x8AC5FF)),
        512:  TileStyle(icon: "🍎", fill: Color(hex: 0xEF9C9E), border: Color(hex: 0xE03A3E)),
        1024: TileStyle(icon: "🥥", fill: Color(hex: 0xCBAC9E), border: Color(hex: 0x965A3E)),
        2048: TileStyle(icon: "🍑", fill: Color(hex: 0xEBA3F7), border: Color(hex: 0xD647F0)),
    ]

    static func style(for value: Int) -> TileStyle {
        map[value] ?? TileStyle(icon: "⭐️", fill: Color(hex: 0xEBA3F7), border: Color(hex: 0xD647F0))
    }
}
