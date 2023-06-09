package com.example.cleanslate.ui.stateholder

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cleanslate.data.datasource.CleanSlateSharedPreferences
import com.example.cleanslate.data.model.*
import com.example.cleanslate.domain.GarbageTypeDeserializer
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.geometry.Point
import java.io.IOException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private var preferences = CleanSlateSharedPreferences(context)

    var location = MutableLiveData<Point>()

    var cameraLocation = MutableLiveData<Point>()
    var cameraZoom = MutableLiveData<Float>()
    var cameraAzimuth = MutableLiveData<Float>()
    var cameraTilt = MutableLiveData<Float>()

    private var wasteCategories = MutableLiveData<Set<String>>()
    private var allSelectedCategoriesFlag = MutableLiveData<Boolean>()

    var language = MutableLiveData<Language>()
    var theme = MutableLiveData<Theme>()

    private var filename = "garbage_collection_points"
    private val garbageCollectionPoints: List<GarbageCollectionPoint>

    init {
        filename += when (context.resources.configuration.locale.language.toString()) {
            "ru" -> "-ru.json"
            else -> "-en.json"
        }
        location.value = preferences.readLocation()

        cameraLocation.value = preferences.readCameraLocation()
        cameraZoom.value = preferences.readCameraZoom()
        cameraAzimuth.value = preferences.readCameraAzimuth()
        cameraTilt.value = preferences.readCameraTilt()

        wasteCategories.value = preferences.readWasteCategories()
        allSelectedCategoriesFlag.value = preferences.readAllSelectedCategoriesFlag()

        language.value = preferences.readLanguage()
        theme.value = preferences.readTheme()

        when (theme.value) {
            Theme.DAY -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.NIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

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

    fun getGarbageCollectionPoints(): List<GarbageCollectionPoint> {
        val list = mutableListOf<GarbageCollectionPoint>()
        for (point in garbageCollectionPoints) {
            if (allSelectedCategoriesFlag.value!!) {
                var flag = true
                for (category in wasteCategories.value!!)
                    if (!point.type.contains(category.toGarbageType()))
                        flag = false
                if (flag) list.add(point)
            } else {
                var flag = false
                for (category in wasteCategories.value!!)
                    if (point.type.contains(category.toGarbageType()))
                        flag = true
                if (flag) list.add(point)
            }
        }

        return list
    }
}