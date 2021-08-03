package ru.serzh272.farmer.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.serzh272.farmer.AppSettings
import ru.serzh272.farmer.MainActivity
import ru.serzh272.farmer.R
import ru.serzh272.farmer.SharedViewModel
import ru.serzh272.farmer.data.local.entities.Field
import ru.serzh272.farmer.databinding.FieldInputDialogBinding
import ru.serzh272.farmer.databinding.FragmentFieldsMapBinding
import ru.serzh272.farmer.extensions.*
import ru.serzh272.farmer.models.MapState
import ru.serzh272.farmer.models.SearchMapObject
import ru.serzh272.farmer.ui.adapters.SearchResultsAdapter
import java.util.*

@AndroidEntryPoint
class FieldsMapFragment : Fragment(), Session.SearchListener {
    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null
    private var newField: Field? = null
    private val args by navArgs<FieldsMapFragmentArgs>()
    private val viewModel: SharedViewModel by activityViewModels()
    private var userLocationLayer: UserLocationLayer? = null
    private lateinit var binding: FragmentFieldsMapBinding
    private val adapter: SearchResultsAdapter
        get() = binding.bottomSheetSearch.bottomSheetSearchLayout.adapter as SearchResultsAdapter
    private val bsb by lazy {
        BottomSheetBehavior.from(binding.bottomSheet.bottomSheetInfoLayout)
    }
    private val bsbSearch by lazy {
        BottomSheetBehavior.from(binding.bottomSheetSearch.bottomSheetSearchLayout)
    }
    private lateinit var searchSession: Session
    private lateinit var searchManager: SearchManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFieldsMapBinding.inflate(layoutInflater, container, false)
        setupMap()
        setupViews()
        viewModel.getFields().observe(viewLifecycleOwner, { fields ->
            updateMap(fields)
        })
        viewModel.getState().observe(viewLifecycleOwner, { state ->
            updateUI(state)
        })
        viewModel.getSearchResult().observe(viewLifecycleOwner, {
            adapter.updateItems(it)
        })
        return binding.root
    }

    override fun onDestroy() {
        val pos = binding.mapView.map.cameraPosition.target
        viewModel.setAppSettings(AppSettings(pos.latitude, pos.longitude))
        super.onDestroy()
    }

    private fun setupMap() {
        val inputListener: InputListener = object : InputListener {
            override fun onMapTap(p0: Map, p1: Point) {

            }

            override fun onMapLongTap(p0: Map, p1: Point) {

            }
        }
        MapKitFactory.initialize(activity)
        SearchFactory.initialize(activity)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow).apply {
                isVisible = true
                isHeadingEnabled = true
            }
        val appSettings = viewModel.getAppSettings()
        binding.mapView.map.run {
            move(
                CameraPosition(
                    Point(
                        args.initPoint?.x ?: appSettings.initLatitude,
                        args.initPoint?.y ?: appSettings.initLongitude
                    ), args.zoom, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.2f),
                null
            )
            addInputListener(inputListener)
        }
    }

    private fun updateUI(state: MapState) {
        with(binding) {
            fabAdd.isVisible = !state.isEditMode && !state.isSearchMode
            fabCurrentPosition.isVisible = !state.isEditMode && !state.isSearchMode
            fabApply.isVisible = state.isEditMode
            mapView.isEditMode = state.isEditMode
            if (!state.isEditMode) {
                binding.mapView.map.mapObjects.removeAllPlacemarks()
            }
            if (state.isSearchMode) {
                bsb.state = BottomSheetBehavior.STATE_HIDDEN
                bsbSearch.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bsbSearch.state = BottomSheetBehavior.STATE_HIDDEN
                adapter.deleteAllItems()
            }
            bottomSheetSearch.bottomSheetSearchLayout.isVisible =
                state.isSearchMode
            bottomSheet.bottomSheetInfoLayout.isVisible = !state.isSearchMode
        }

    }

    private fun updateBottomSheet(fld: Field) {
        with(binding.bottomSheet) {
            tvName.text = getString(R.string.name_label_text, fld.name)
            tvCulture.text = getString(R.string.culture_label_text, fld.culture)
            tvSowingDate.text =
                getString(R.string.sowing_date_label_text, fld.sowingDate.format("dd.MM.yyyy"))
        }
    }

    private fun updateMap(fields: List<Field>) {
        val mapObjTapListener = MapObjectTapListener { obj, p ->
            searchMenuItem?.collapseActionView()
            viewModel.handleSearchMode(false)
            binding.mapView.map.mapObjects.removeAllPlacemarks()
            binding.mapView.map.mapObjects.addPlacemark(
                p,
                ImageProvider.fromResource(requireContext(), R.mipmap.ic_field_info)
            )
            updateBottomSheet(obj.userData as Field)
            bsb.state = BottomSheetBehavior.STATE_COLLAPSED
            bsbSearch.state = BottomSheetBehavior.STATE_HIDDEN
            val scrP = p.toScreenPoint(binding.mapView)
            if (scrP != null) {
                if (scrP.y > binding.mapView.measuredHeight * 0.75) {
                    scrP.offset(0f, 0.25f * binding.mapView.measuredHeight)
                    val curCamPos = binding.mapView.map.cameraPosition
                    binding.mapView.map.move(
                        CameraPosition(
                            binding.mapView.pointFToMapPoint(scrP)!!,
                            curCamPos.zoom,
                            curCamPos.azimuth,
                            curCamPos.tilt
                        ), Animation(Animation.Type.SMOOTH, 0.5f)
                    ) {

                    }
                }
            }
            true
        }
        binding.mapView.map.mapObjects.clear()
        for (fld in fields) {
            binding.mapView.map.mapObjects.addField(fld.points.map {
                it.toMapPoint()
            }, fld.color).apply {
                addTapListener(mapObjTapListener)
                userData = fld
            }
        }
        bsb.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupViews() {
        with(binding) {
            fabCurrentPosition.setOnClickListener {
                val permissionStatus = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    mapView.map.run {
                        userLocationLayer?.cameraPosition()?.target?.let { pnt ->
                            CameraPosition(
                                pnt, args.zoom, 0.0f, 0.0f
                            )
                        }?.let { pos ->
                            move(
                                pos,
                                Animation(Animation.Type.SMOOTH, 2f),
                                null
                            )
                        }
                    }
                } else {
                    activity?.let { activity ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MainActivity.REQUEST_CODE_PERMISSION_LOCATION
                        )
                    }

                }

            }
            fabAdd.setOnClickListener {
                viewModel.handleChangeEditMode(true)
                showFieldDialog { fld ->
                    newField = fld
                }
            }
            fabApply.setOnClickListener {
                mapView.applyField { pts ->
                    newField?.let { fld -> viewModel.addField(fld.copy(points = pts)) }
                }
                viewModel.handleChangeEditMode(false)
            }

            bsb.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> viewModel.handleChangeEditMode(false)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    fabAdd.animateFab(slideOffset)
                    fabApply.animateFab(slideOffset)
                    fabCurrentPosition.animateFab(slideOffset)
                }

            })
            with(bottomSheetSearch.bottomSheetSearchLayout) {
                adapter = SearchResultsAdapter { findedObj ->
                    mapView.map.run {
                        move(
                            CameraPosition(
                                findedObj.location, 11.0f, 0.0f, 0.0f
                            ),
                            Animation(Animation.Type.SMOOTH, 2f),
                            null
                        )
                    }
                    bsbSearch.state = BottomSheetBehavior.STATE_HIDDEN
                }
                layoutManager =
                    LinearLayoutManager(requireContext())
            }

            bottomSheet.btnClose.setOnClickListener {
                binding.mapView.map.mapObjects.removeAllPlacemarks()
                bsb.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

    }


    private fun FloatingActionButton.animateFab(slideOffset: Float) {
        if (slideOffset > 0.5) {
            this.animate()
                .scaleX(MathUtils.clamp(1f - 4 * (slideOffset - 0.5f), 0f, 1f))
                .scaleY(MathUtils.clamp(1f - 4 * (slideOffset - 0.5f), 0f, 1f))
                .setDuration(0)
                .start()
        } else {
            this.scaleX = 1f
            this.scaleY = 1f
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onSearchResponse(response: Response) {
        val searchMapObjects = mutableListOf<SearchMapObject>()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            val name = searchResult.obj!!.name
            val description = searchResult.obj!!.descriptionText

            if (resultLocation != null) {
                searchMapObjects.add(SearchMapObject(name, description, resultLocation))
            }
        }
        viewModel.updateSearchResult(searchMapObjects)
    }

    override fun onSearchError(p0: Error) {
        Snackbar.make(requireContext(), binding.root, "Search Error", Snackbar.LENGTH_SHORT).show()
    }

    private fun showFieldDialog(result: (Field?) -> Unit) {
        val dlgBinding = FieldInputDialogBinding.inflate(layoutInflater)

        val dlg = AlertDialog.Builder(requireContext())
            .setTitle(context?.getString(R.string.add_field_dialog_title))
            .setView(dlgBinding.root)
            .setPositiveButton("Ok", null)
            .setNegativeButton("Cancel") { _, _ ->
                viewModel.handleChangeEditMode(false)
                result(null)
            }
            .setOnCancelListener {
                viewModel.handleChangeEditMode(false)
                result(null)
            }
            .create()
        dlg.setOnShowListener {
            val button: Button = (dlg as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { //Dismiss once everything is OK.
                when {
                    dlgBinding.tiTextName.text.isNullOrBlank() -> {
                        dlgBinding.textNameInputLayout.error =
                            requireContext().getString(R.string.error_blank_name)

                    }
                    dlgBinding.tiTextCulture.text.isNullOrBlank() -> {
                        dlgBinding.textCultureInputLayout.error =
                            requireContext().getString(R.string.error_blank_culture)

                    }
                    else -> {
                        dlgBinding.textNameInputLayout.error = null
                        dlgBinding.textCultureInputLayout.error = null
                        result(
                            Field(
                                0,
                                dlgBinding.tiTextName.text.toString(),
                                dlgBinding.tiTextCulture.text.toString(),
                                dlgBinding.datePicker.run {
                                    Date(year - 1900, month, dayOfMonth)
                                },
                                Color.RED,
                                listOf(),
                                binding.mapView.map.cameraPosition.zoom
                            )
                        )
                        dlg.dismiss()
                    }
                }
            }
        }
        dlg.show()
        dlgBinding.textNameInputLayout.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem?.actionView as SearchView
        super.onCreateOptionsMenu(menu, inflater)
        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return searchObjects(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (bsbSearch.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bsbSearch.state = BottomSheetBehavior.STATE_EXPANDED
                }

                return searchObjects(newText)
            }
        })
    }

    private fun searchObjects(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(binding.mapView.map.visibleRegion),
                SearchOptions(),
                this@FieldsMapFragment
            )
            return true
        }
        return false
    }
}