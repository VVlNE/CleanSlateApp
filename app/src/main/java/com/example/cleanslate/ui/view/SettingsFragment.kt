package com.example.cleanslate.ui.view

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.cleanslate.R
import com.example.cleanslate.data.model.Language
import com.example.cleanslate.data.model.Theme
import com.example.cleanslate.databinding.FragmentSettingsBinding
import com.example.cleanslate.ui.stateholder.SettingsViewModel
import com.google.android.material.chip.Chip

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        binding.title.text = resources.getString(R.string.settings)

        viewModel.language.observe(viewLifecycleOwner, Observer {
            when (it) {
                Language.ENGLISH -> binding.languageSetting.check(binding.englishLanguage.id)
                Language.RUSSIAN -> binding.languageSetting.check(binding.russianLanguage.id)
                Language.SYSTEM -> binding.languageSetting.check(binding.systemLanguage.id)
            }
        })

        viewModel.theme.observe(viewLifecycleOwner, Observer {
            when (it) {
                Theme.DAY -> binding.themeSetting.check(binding.dayTheme.id)
                Theme.NIGHT -> binding.themeSetting.check(binding.nightTheme.id)
                Theme.SYSTEM -> binding.themeSetting.check(binding.systemTheme.id)
            }
        })

        binding.languageSetting.setOnCheckedChangeListener { group, checkedId ->
            when (group.findViewById<Chip?>(checkedId)) {
                binding.englishLanguage -> viewModel.changeLanguage(Language.ENGLISH)
                binding.russianLanguage -> viewModel.changeLanguage(Language.RUSSIAN)
                binding.systemLanguage -> viewModel.changeLanguage(Language.SYSTEM)
            }
        }

        binding.themeSetting.setOnCheckedChangeListener { group, checkedId ->
            when (group.findViewById<Chip?>(checkedId)) {
                binding.dayTheme -> viewModel.changeTheme(Theme.DAY)
                binding.nightTheme -> viewModel.changeTheme(Theme.NIGHT)
                binding.systemTheme -> viewModel.changeTheme(Theme.SYSTEM)
            }
        }

        setBackButton()
    }

    private fun setBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_settings_to_navigation_main)
        }
    }
}