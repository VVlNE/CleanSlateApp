package com.example.cleanslate.ui.stateholder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cleanslate.data.datasource.CleanSlateSharedPreferences

class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private var preferences = CleanSlateSharedPreferences(context)

    var wasteCategories = MutableLiveData<Set<String>>()
    var allSelectedCategoriesFlag = MutableLiveData<Boolean>()

    init {
        wasteCategories.value = preferences.readWasteCategories()
        allSelectedCategoriesFlag.value = preferences.readAllSelectedCategoriesFlag()
    }

    fun changeAllSelectedCategoriesFlag(flag: Boolean) =
        preferences.saveAllSelectedCategoriesFlag(flag)

    fun changeWasteCategories(set: Set<String>) = preferences.saveWasteCategories(set)
}