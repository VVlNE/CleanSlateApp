package com.example.cleanslate.ui.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.cleanslate.R
import com.example.cleanslate.data.model.GarbageType
import com.example.cleanslate.data.model.toGarbageType
import com.example.cleanslate.databinding.FragmentFiltersBinding
import com.example.cleanslate.data.network.FileDataPart
import com.example.cleanslate.data.network.VolleyFileUploadRequest
import com.example.cleanslate.ui.stateholder.FiltersViewModel
import java.io.*
import java.util.*

class FiltersFragment : Fragment() {
    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FiltersViewModel

    val REQUEST_IMAGE = 1

    //TODO("Нужно установить правильный адрес сервера")
    private val URL = "http://192.168.0.16:5000" + "/predictClass"

    companion object {
        fun newInstance() = FiltersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[FiltersViewModel::class.java]
        binding.title.text = resources.getString(R.string.filters)

        if (!checkPermissions()) requestPermissions()

        setFilters()

        setBackButton()
        setAutoDeterminationButton()
        setApplyButton()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun setBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_filters_to_navigation_main)
        }
    }

    private fun setAutoDeterminationButton() {
        binding.autoDeterminationButton.setOnClickListener {
            openCamera()
        }
    }

    private fun setApplyButton() {
        binding.applyButton.setOnClickListener {
            viewModel.changeAllSelectedCategoriesFlag(binding.allSelectedCategoriesButton.isChecked)

            val wasteCategories = mutableSetOf<String>()
            if (binding.paper.isChecked) wasteCategories.add(GarbageType.PAPER.toString())
            if (binding.glass.isChecked) wasteCategories.add(GarbageType.GLASS.toString())
            if (binding.plastic.isChecked) wasteCategories.add(GarbageType.PLASTIC.toString())
            if (binding.metal.isChecked) wasteCategories.add(GarbageType.METAL.toString())
            viewModel.changeWasteCategories(wasteCategories)

            findNavController().navigate(R.id.action_navigation_filters_to_navigation_main)
        }
    }

    private fun postRequest(image: ByteArray, url: String) {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            url,
            Response.Listener {
                binding.paper.isChecked = false
                binding.glass.isChecked = false
                binding.plastic.isChecked = false
                binding.metal.isChecked = false
                when (it.data.toString(Charsets.UTF_8).toGarbageType()) {
                    GarbageType.PAPER -> binding.paper.isChecked = true
                    GarbageType.GLASS -> binding.glass.isChecked = true
                    GarbageType.PLASTIC -> binding.plastic.isChecked = true
                    GarbageType.METAL -> binding.metal.isChecked = true
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    requireContext(),
                    R.string.server_is_not_available,
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["external_image"] = FileDataPart("image", image, "jpeg")
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun Bitmap.toByteArray(): ByteArray {
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG, 100, this)
            return toByteArray()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (checkPermissions())
                startActivityForResult(takePictureIntent, REQUEST_IMAGE)
            else Toast.makeText(
                requireContext(),
                R.string.camera_is_not_available,
                Toast.LENGTH_LONG
            ).show()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                R.string.no_camera,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                val image = data!!.extras!!.get("data") as Bitmap
                postRequest(image.toByteArray(), URL)
            }
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                0
            )
        }
    }

    private fun checkPermissions() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun setFilters() {
        for (category in viewModel.wasteCategories.value!!)
            when (category.toGarbageType()) {
                GarbageType.PLASTIC -> binding.plastic.isChecked = true
                GarbageType.PAPER -> binding.paper.isChecked = true
                GarbageType.METAL -> binding.metal.isChecked = true
                GarbageType.GLASS -> binding.glass.isChecked = true
            }

        binding.allSelectedCategoriesButton.isChecked = viewModel.allSelectedCategoriesFlag.value!!
    }
}