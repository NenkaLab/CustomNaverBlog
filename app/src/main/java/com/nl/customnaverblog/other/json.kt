package com.nl.customnaverblog.other

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonSerializer @Inject constructor() {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
        coerceInputValues = true
        allowTrailingComma = true
        allowComments = true
        allowSpecialFloatingPointValues = true
    }

    fun <T> encodeToString(serializer: KSerializer<T>, value: T): String {
        return json.encodeToString(serializer, value)
    }

    fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return json.decodeFromString(deserializer, string)
    }
}