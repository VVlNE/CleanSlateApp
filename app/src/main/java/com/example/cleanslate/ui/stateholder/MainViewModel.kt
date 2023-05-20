package com.example.cleanslate.ui.stateholder

import android.app.Application
import android.graphics.PointF
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cleanslate.R
import com.example.cleanslate.data.datasource.CleanSlateSharedPreferences
import com.example.cleanslate.data.model.GarbageCollectionPoint
import com.example.cleanslate.data.model.GarbageType
import com.example.cleanslate.data.model.Language
import com.example.cleanslate.domain.GarbageTypeDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import java.io.IOException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private var preferences = CleanSlateSharedPreferences(context)

    var location = MutableLiveData<Point>()

    var cameraLocation = MutableLiveData<Point>()
    var cameraZoom = MutableLiveData<Float>()
    var cameraAzimuth = MutableLiveData<Float>()
    var cameraTilt = MutableLiveData<Float>()

    private val filename = "garbage_collection_points.json"
    val garbageCollectionPoints: List<GarbageCollectionPoint>

    init {
        location.value = preferences.readLocation()

        cameraLocation.value = preferences.readCameraLocation()
        cameraZoom.value = preferences.readCameraZoom()
        cameraAzimuth.value = preferences.readCameraAzimuth()
        cameraTilt.value = preferences.readCameraTilt()

        val jsonFileString = getJsonDataFromAsset()
        val myList = object : TypeToken<List<GarbageCollectionPoint>>() {}.type
        val gson = GsonBuilder()
            .registerTypeAdapter(GarbageType::class.java, GarbageTypeDeserializer())
            .create()
        garbageCollectionPoints =
            if (jsonFileString != null) gson.fromJson(jsonFileString, myList) else listOf()
    }

    private fun getJsonDataFromAsset(): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            return null
        }
        return jsonString
    }

    fun changeLocation(location: Point?) =
        location?.also { preferences.saveLocation(location) } ?: this.location.value!!

    fun changeCameraPosition(location: Point, zoom: Float, azimuth: Float, tilt: Float) {
        preferences.saveCameraLocation(location)
        preferences.saveCameraZoom(zoom)
        preferences.saveCameraAzimuth(azimuth)
        preferences.saveCameraTilt(tilt)
    }
}