package com.example.cleanslate.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.example.cleanslate.data.model.GarbageType
import com.example.cleanslate.data.model.Language
import com.example.cleanslate.data.model.Theme
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point

class CleanSlateSharedPreferences(context: Context) {
    private val preferencesName = "CleanSlateSharedPreferences"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    private val languageKey = "Language"
    private val themeKey = "Theme"

    private val locationKey = "Location"
    private val cameraLocationKey = "CameraLocation"
    private val cameraZoomKey = "CameraZoom"
    private val cameraAzimuthKey = "CameraAzimuth"
    private val cameraTiltKey = "CameraTilt"
    private val wasteCategoriesKey = "WasteCategories"
    private val allSelectedCategoriesFlagKey = "AllSelectedCategoriesFlag"

    fun saveLanguage(language: Language) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(languageKey, Gson().toJson(language))
        editor.apply()
    }

    fun readLanguage(): Language {
        if (!preferences.contains(languageKey))
            saveLanguage(Language.SYSTEM)

        return Gson().fromJson(preferences.getString(languageKey, ""), Language::class.java)
    }

    fun saveTheme(theme: Theme) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(themeKey, Gson().toJson(theme))
        editor.apply()
    }

    fun readTheme(): Theme {
        if (!preferences.contains(themeKey))
            saveTheme(Theme.SYSTEM)

        return Gson().fromJson(preferences.getString(themeKey, ""), Theme::class.java)
    }

    fun saveLocation(location: Point) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(locationKey, Gson().toJson(location))
        editor.apply()
    }

    fun readLocation(): Point {
        if (!preferences.contains(locationKey))
            saveLocation(Point(55.755864, 37.617698))

        return Gson().fromJson(preferences.getString(locationKey, ""), Point::class.java)
    }

    fun saveCameraLocation(cameraLocation: Point) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString(cameraLocationKey, Gson().toJson(cameraLocation))
        editor.apply()
    }

    fun readCameraLocation(): Point {
        if (!preferences.contains(cameraLocationKey))
            saveCameraLocation(Point(55.755864, 37.617698))

        return Gson().fromJson(preferences.getString(cameraLocationKey, ""), Point::class.java)
    }

    fun saveCameraZoom(cameraZoom: Float) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putFloat(cameraZoomKey, cameraZoom)
        editor.apply()
    }

    fun readCameraZoom(): Float {
        if (!preferences.contains(cameraZoomKey))
            saveCameraZoom(16.0f)

        return preferences.getFloat(cameraZoomKey, 16.0f)
    }

    fun saveCameraAzimuth(cameraAzimuth: Float) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putFloat(cameraAzimuthKey, cameraAzimuth)
        editor.apply()
    }

    fun readCameraAzimuth(): Float {
        if (!preferences.contains(cameraAzimuthKey))
            saveCameraAzimuth(0.0f)

        return preferences.getFloat(cameraAzimuthKey, 0.0f)
    }

    fun saveCameraTilt(cameraTilt: Float) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putFloat(cameraTiltKey, cameraTilt)
        editor.apply()
    }

    fun readCameraTilt(): Float {
        if (!preferences.contains(cameraTiltKey))
            saveCameraTilt(0.0f)

        return preferences.getFloat(cameraTiltKey, 0.0f)
    }

    fun saveWasteCategories(wasteCategories: Set<String>) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putStringSet(wasteCategoriesKey, wasteCategories)
        editor.apply()
    }

    fun readWasteCategories(): Set<String> {
        if (!preferences.contains(wasteCategoriesKey)) {
            val set = mutableSetOf<String>()
            for (value in GarbageType.values())
                set.add(value.toString())
            saveWasteCategories(set)
        }

        return preferences.getStringSet(wasteCategoriesKey, setOf<String>())!!
    }

    fun saveAllSelectedCategoriesFlag(allSelectedCategoriesFlag: Boolean) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(allSelectedCategoriesFlagKey, allSelectedCategoriesFlag)
        editor.apply()
    }

    fun readAllSelectedCategoriesFlag(): Boolean {
        if (!preferences.contains(allSelectedCategoriesFlagKey))
            saveAllSelectedCategoriesFlag(false)

        return preferences.getBoolean(allSelectedCategoriesFlagKey, false)
    }
}