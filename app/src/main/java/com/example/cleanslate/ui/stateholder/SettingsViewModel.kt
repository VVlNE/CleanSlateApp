package com.example.cleanslate.ui.stateholder

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cleanslate.data.datasource.CleanSlateSharedPreferences
import com.example.cleanslate.data.model.Language
import com.example.cleanslate.data.model.Theme
import com.yandex.mapkit.MapKitFactory

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private var preferences = CleanSlateSharedPreferences(context)

    var language = MutableLiveData<Language>()
    var theme = MutableLiveData<Theme>()

    init {
        language.value = preferences.readLanguage()
        theme.value = preferences.readTheme()
    }

    // TODO("Настроить смену языков")
    fun changeLanguage(language: Language) {
        this.language.value = language
        preferences.saveLanguage(language)
    }

    // TODO("Не работает с картами")
    fun changeTheme(theme: Theme) {
        this.theme.value = theme
        preferences.saveTheme(theme)

        /*
        when (theme) {
            Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.NIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }*/
    }
}