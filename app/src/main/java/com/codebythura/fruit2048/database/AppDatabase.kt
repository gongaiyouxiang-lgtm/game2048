package com.codebythura.fruit2048.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [GameStateEntity::class ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(GridConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val gameStateDao: GameStateDao
}