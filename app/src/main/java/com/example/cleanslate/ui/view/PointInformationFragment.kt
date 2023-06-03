package com.example.cleanslate.ui.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cleanslate.R
import com.example.cleanslate.databinding.FragmentPointInformationBinding
import com.example.cleanslate.ui.stateholder.PointInformationViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_settings.view.*

class PointInformationFragment : Fragment() {
    private var _binding: FragmentPointInformationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PointInformationViewModel

    companion object {
        fun newInstance() = PointInformationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPointInformationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[PointInformationViewModel::class.java]

        binding.title.text = arguments?.getString("name")

        showTitleName()

        for (type in arguments?.getStringArray("type")!!)
            binding.wasteCategories.addChip(requireContext(), type)

        binding.address.text = arguments?.getString("address")
        binding.schedule.text = arguments?.getString("schedule")

        binding.telephone.visibility =
            if (arguments?.getString("telephone") == null) View.GONE
            else View.VISIBLE
        binding.telephone.text = arguments?.getString("telephone")

        binding.email.visibility =
            if (arguments?.getString("email") == null) View.GONE
            else View.VISIBLE
        binding.email.text = arguments?.getString("email")

        binding.website.visibility =
            if (arguments?.getString("website") == null) View.GONE
            else View.VISIBLE
        binding.website.text = arguments?.getString("website")

        binding.distance.text = String.format("%.2f", arguments?.getDouble("distance")) +
                " " + resources.getString(R.string.kilometers)

        setBackButton()
        setThirdPartyMapButton()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun ChipGroup.addChip(context: Context, label: String) {
        Chip(context, null, R.style.ChipStyle).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = label
            isClickable = false
            isCheckable = false
            addView(this)
        }
    }

    private fun setBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_point_information_to_navigation_main)
        }
    }

    private fun setThirdPartyMapButton() {
        binding.thirdPartyMapButton.setOnClickListener {
            val uri = Uri.parse(arguments?.getString("coordinates"))
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
            }
            startActivity(intent)
        }
    }

    private fun showTitleName() {
        binding.title.setOnClickListener {
            Toast.makeText(requireContext(), binding.title.text.toString(), Toast.LENGTH_LONG).show()
        }
    }
}