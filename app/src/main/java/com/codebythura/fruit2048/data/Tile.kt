package com.codebythura.fruit2048.data

import androidx.compose.ui.graphics.Color

data class Tile(val value: Int, val icon: String, val color: Color, val borderColor: Color)

val lightTileMap = mapOf<Int, Tile>(
    2 to Tile(value = 2, icon = "\uD83C\uDF49", color = Color(0xFFb5e5bb), borderColor = Color(0xFF6bcb77)),
    4 to Tile(4, icon = "\uD83C\uDF53", color = Color(0xFFffbcdf), borderColor = Color(0xFFff7abe)),
    8 to Tile(8, icon = "\uD83C\uDF4C", color = Color(0xFFF3E56C), borderColor = Color(0xFFEFD54A)),
    16 to Tile(16, icon = "\uD83C\uDF4A", color = Color(0xFFf6bf9e), borderColor = Color(0xFFed803d)),
    32 to Tile(32, icon = "\uD83C\uDF47", color = Color(0xFFcba6ff), borderColor = Color(0xFF974dff)),
    64 to Tile(64, icon = "\uD83E\uDD6D", color = Color(0xFFffdb80), borderColor = Color(0xFFffb600)),
    128 to Tile(128, icon = "\uD83E\uDD5D", color = Color(0xFFdbe4a6), borderColor = Color(0xFFb8c94d)),
    256 to Tile(256, icon = "\uD83E\uDED0", color = Color(0xFFc5e2ff), borderColor = Color(0xFF8ac5ff)),
    512 to Tile(512, icon = "\uD83C\uDF4E", color = Color(0xFFef9c9e), borderColor = Color(0xFFe03a3e)),
    1024 to Tile(1024, icon = "\uD83E\uDD65", color = Color(0xFFcbac9e), borderColor = Color(0xFF965a3e)),
    2048 to Tile(2048, icon = "\uD83C\uDF51", color = Color(0xFFeba3f7), borderColor = Color(0xFFd647f0)),
)

val darkTileMap = mapOf<Int, Tile>(
    2 to Tile(value = 2, icon = "\uD83C\uDF49", color = Color(0xFF78B678), borderColor = Color(0xFF308F3B)),
    4 to Tile(4, icon = "\uD83C\uDF53", color = Color(0xFFE1A1B8), borderColor = Color(0xFFBD568A)),
    8 to Tile(8, icon = "\uD83C\uDF4C", color = Color(0xFFD7C45A), borderColor = Color(0xFFBEA736)),
    16 to Tile(16, icon = "\uD83C\uDF4A", color = Color(0xFFD39978), borderColor = Color(0xFFBE6530)),
    32 to Tile(32, icon = "\uD83C\uDF47", color = Color(0xFF9578C0), borderColor = Color(0xFF7633CE)),
    64 to Tile(64, icon = "\uD83E\uDD6D", color = Color(0xFFE3C067), borderColor = Color(0xFFD79C03)),
    128 to Tile(128, icon = "\uD83E\uDD5D", color = Color(0xFFC2CE8E), borderColor = Color(0xFF9CAD33)),
    256 to Tile(256, icon = "\uD83E\uDED0", color = Color(0xFFA7BFDA), borderColor = Color(0xFF6496C5)),
    512 to Tile(512, icon = "\uD83C\uDF4E", color = Color(0xFFC78586), borderColor = Color(0xFFBB282C)),
    1024 to Tile(1024, icon = "\uD83E\uDD65", color = Color(0xFFA6897D), borderColor = Color(0xFF7A482F)),
    2048 to Tile(2048, icon = "\uD83C\uDF51", color = Color(0xFFB77CC2), borderColor = Color(0xFFB433CC)),
)
