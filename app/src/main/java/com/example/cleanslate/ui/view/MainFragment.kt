package com.example.cleanslate.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cleanslate.R
import com.example.cleanslate.data.model.GarbageCollectionPoint
import com.example.cleanslate.data.model.GarbageType
import com.example.cleanslate.databinding.FragmentMainBinding
import com.example.cleanslate.ui.stateholder.MainViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var userLocationLayer: UserLocationLayer
    private val mapObjectList = mutableListOf<PlacemarkMapObject>()

    private lateinit var location: Point
    private var cameraPosition = CameraPosition()

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.title.text = resources.getString(R.string.map)

        if (!checkPermissions()) requestPermissions()

        mapObjectCollection = binding.mapView.map.mapObjects.addCollection()
        if (checkPermissions()) addUserLocationLayer()

        moveToCameraLocation()

        addMapObjects()

        setMyLocationButton()

        mapObjectCollection.addTapListener(listener)

        setSettingsButton()

        setFiltersButton()
    }

    override fun onStop() {
        if (checkPermissions()) viewModel.changeLocation(
            userLocationLayer.cameraPosition()?.let { it.target })

        changeCameraPosition()

        binding.mapView.onStop()

        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        binding.mapView.onStart()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    private fun checkPermissions() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun addUserLocationLayer() {
        userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)

        userLocationLayer.apply {
            isVisible = true
            isHeadingEnabled = false

            setAnchor(
                PointF((binding.mapView.width * 0.5f), (binding.mapView.height * 0.5f)),
                PointF((binding.mapView.width * 0.5f), (binding.mapView.height * 0.83f))
            )
            resetAnchor()
        }
    }

    private fun moveToCameraLocation() = binding.mapView.map.move(
        CameraPosition(
            viewModel.cameraLocation.value!!,
            viewModel.cameraZoom.value!!,
            viewModel.cameraAzimuth.value!!,
            viewModel.cameraTilt.value!!
        ),
        Animation(Animation.Type.SMOOTH, 0.0f),
        null
    )

    private fun moveToLocation() {
        viewModel.location.observe(viewLifecycleOwner, Observer {
            binding.mapView.map.move(
                CameraPosition(
                    viewModel.changeLocation(userLocationLayer.cameraPosition()?.target),
                    16.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 2.0f),
                null
            )
        })

    }

    // TODO("Добавить изображение маркера")
    private fun addMapObjects() {
        val image = View(requireContext()).apply {
            background = requireContext().getDrawable(R.drawable.ic_baseline_place)
        }

        for ((index, garbageCollectionPoint) in viewModel.getGarbageCollectionPoints()
            .withIndex()) {
            val mapObject = mapObjectCollection.addPlacemark(
                garbageCollectionPoint.coordinates,
                ViewProvider(image)
            )
            mapObject.userData = garbageCollectionPoint
            mapObjectList.add(mapObject)
        }
    }

    private fun setMyLocationButton() {
        binding.myLocationButton.setOnClickListener {
            if (checkPermissions()) moveToLocation()
            else requestPermissions()
        }
    }

    private val listener = MapObjectTapListener { mapObject, _ ->
        val garbageCollectionPoint = (mapObject.userData as? GarbageCollectionPoint)!!

        var typeArray = arrayOf<String>()
        for (type in garbageCollectionPoint.type)
            typeArray += when (type) {
                GarbageType.PAPER -> resources.getString(R.string.paper)
                GarbageType.GLASS -> resources.getString(R.string.glass)
                GarbageType.PLASTIC -> resources.getString(R.string.plastic)
                GarbageType.METAL -> resources.getString(R.string.metal)
            }

        val bundle = Bundle()
        bundle.putString("name", garbageCollectionPoint.name)
        bundle.putStringArray("type", typeArray)
        bundle.putString("address", garbageCollectionPoint.address)
        bundle.putString("schedule", garbageCollectionPoint.schedule)
        bundle.putString("telephone", garbageCollectionPoint.telephone)
        bundle.putString("email", garbageCollectionPoint.email)
        bundle.putString("website", garbageCollectionPoint.website)
        bundle.putString(
            "coordinates",
            "geo:0,0?q=${garbageCollectionPoint.coordinates.latitude}, ${garbageCollectionPoint.coordinates.longitude}"
        )

        bundle.putDouble(
            "distance",
            garbageCollectionPoint.getDistanceFrom(viewModel.location.value!!)
        )

        findNavController().navigate(
            R.id.action_navigation_main_to_navigation_point_information,
            bundle
        )
        true
    }

    private fun changeCameraPosition() {
        viewModel.changeCameraPosition(
            binding.mapView.map.cameraPosition.target,
            binding.mapView.map.cameraPosition.zoom,
            binding.mapView.map.cameraPosition.azimuth,
            binding.mapView.map.cameraPosition.tilt
        )
    }

    private fun setSettingsButton() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_main_to_navigation_settings)
        }
    }

    private fun setFiltersButton() {
        binding.filtersButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_main_to_navigation_filters)
        }
    }
}