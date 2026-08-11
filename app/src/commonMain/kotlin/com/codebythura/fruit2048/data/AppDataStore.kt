package com.codebythura.fruit2048.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlinx.serialization.json.Json

/** Multiplatform JSON serializer for [AppData], okio-backed so it runs on Android and iOS. */
object AppDataSerializer : OkioSerializer<AppData> {

    override val defaultValue: AppData = AppData()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun readFrom(source: BufferedSource): AppData =
        try {
            json.decodeFromString(AppData.serializer(), source.readUtf8())
        } catch (exception: Exception) {
            // A blank/corrupt file falls back to defaults rather than crashing.
            defaultValue
        }

    override suspend fun writeTo(value: AppData, sink: BufferedSink) {
        sink.writeUtf8(json.encodeToString(AppData.serializer(), value))
    }
}

/**
 * Builds the single app [DataStore]. Each platform supplies where the JSON file lives
 * ([producePath]) — Android: app files dir; iOS: NSDocumentDirectory.
 */
fun createAppDataStore(producePath: () -> String): DataStore<AppData> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = AppDataSerializer,
            producePath = { producePath().toPath() },
        ),
    )
