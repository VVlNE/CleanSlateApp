package com.example.cleanslate.domain

import com.example.cleanslate.data.model.GarbageType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class GarbageTypeDeserializer : JsonDeserializer<GarbageType> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GarbageType {
        val stringValue = json?.asString?.toUpperCase()
        for (value in GarbageType.values()) {
            if (value.toString() == stringValue) {
                return value
            }
        }
        throw IllegalArgumentException("Unknown type $stringValue!")
    }
}