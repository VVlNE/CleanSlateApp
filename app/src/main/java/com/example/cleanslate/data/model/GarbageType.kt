package com.example.cleanslate.data.model

import java.lang.Exception

enum class GarbageType {
    PAPER,
    GLASS,
    PLASTIC,
    METAL
}

fun String.toGarbageType(): GarbageType? = try {
    GarbageType.valueOf(this.toUpperCase())
} catch (e: Exception) {
    null
}