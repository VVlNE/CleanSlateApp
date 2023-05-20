package com.example.cleanslate.ui.stateholder

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

}