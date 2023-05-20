package com.example.cleanslate.data.model

import com.yandex.mapkit.geometry.Point
import kotlin.math.*

data class GarbageCollectionPoint(
    val name: String,
    val type: Array<GarbageType>,
    val address: String,
    val coordinates: Point,
    val schedule: String,
    val telephone: String,
    val email: String,
    val website: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GarbageCollectionPoint

        if (name != other.name) return false
        if (!type.contentEquals(other.type)) return false
        if (address != other.address) return false
        if (coordinates != other.coordinates) return false
        if (schedule != other.schedule) return false
        if (telephone != other.telephone) return false
        if (email != other.email) return false
        if (website != other.website) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.contentHashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + coordinates.hashCode()
        result = 31 * result + schedule.hashCode()
        result = 31 * result + telephone.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + website.hashCode()
        return result
    }

    fun getDistanceFrom(location: Point): Double {
        var lat1 = location.latitude
        var lat2 = coordinates.latitude
        var lon1 = location.longitude
        var lon2 = coordinates.longitude

        var earthRadiusKm = 6371

        var dLat = degreesToRadians(lat2 - lat1)
        var dLon = degreesToRadians(lon2 - lon1)

        lat1 = degreesToRadians(lat1)
        lat2 = degreesToRadians(lat2)

        var a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        var c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c;
    }

    private fun degreesToRadians(degrees: Double) = degrees * Math.PI / 180
}