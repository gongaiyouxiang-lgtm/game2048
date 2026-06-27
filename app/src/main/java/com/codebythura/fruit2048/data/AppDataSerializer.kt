package com.codebythura.fruit2048.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.codebythura.fruit2048.AppData
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AppDataSerializer : Serializer<AppData> {

    override val defaultValue: AppData = AppData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppData {
        try {
            return AppData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppData, output: OutputStream) {
        return t.writeTo(output)
    }

}