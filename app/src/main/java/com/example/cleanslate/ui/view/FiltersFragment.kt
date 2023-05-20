package com.example.cleanslate.ui.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.*
import java.util.*

class FiltersFragment : Fragment() {
    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FiltersViewModel

    val REQUEST_IMAGE = 1

    //TODO("Нужно установить правильный адрес сервера")
    private val URL = "http://192.168.0.12:5000" + "/predictClass"

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

        setBackButton()

        setAutoDeterminationButton()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun ChipGroup.addChip(context: Context, label: String) {
        Chip(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = label
            isClickable = true
            isCheckable = true
            addView(this)
        }
    }

    private fun setBackButton() {
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_filters_to_navigation_main)
        }
    }

    private fun setAutoDeterminationButton() {
        binding.autoDeterminationButton.setOnClickListener {
            openCamera()
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
                // TODO("Дописать error it.toString()")
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
        } catch (e: ActivityNotFoundException) {
            // TODO("Дописать ошибку")
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
}