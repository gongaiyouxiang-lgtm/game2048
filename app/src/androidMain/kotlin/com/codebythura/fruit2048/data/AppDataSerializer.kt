package com.codebythura.fruit2048.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/** JSON-backed DataStore serializer for [AppData] (KMP-friendly, no protobuf codegen). */
object AppDataSerializer : Serializer<AppData> {

    override val defaultValue: AppData = AppData()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun readFrom(input: InputStream): AppData {
        return try {
            json.decodeFromString(AppData.serializer(), input.readBytes().decodeToString())
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read AppData.", exception)
        }
    }

    override suspend fun writeTo(t: AppData, output: OutputStream) {
        output.write(json.encodeToString(AppData.serializer(), t).encodeToByteArray())
    }
}
