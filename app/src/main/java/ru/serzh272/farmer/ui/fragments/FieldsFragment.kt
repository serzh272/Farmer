package ru.serzh272.farmer.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.serzh272.farmer.R
import ru.serzh272.farmer.SharedViewModel
import ru.serzh272.farmer.databinding.FragmentFieldsBinding
import ru.serzh272.farmer.data.local.entities.Field
import ru.serzh272.farmer.ui.adapters.FieldItemTouchHelperCallback
import ru.serzh272.farmer.ui.adapters.FieldsAdapter

@AndroidEntryPoint
class FieldsFragment : Fragment() {
    private lateinit var binding: FragmentFieldsBinding
    private val viewModel:SharedViewModel by activityViewModels()
    private lateinit var fieldsAdapter:FieldsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fieldsAdapter = FieldsAdapter {
            val act = FieldsFragmentDirections.actionFieldsFragmentToFieldsMapFragment(it.getCenter(), it.zoom)
            findNavController().navigate(act)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFieldsBinding.inflate(inflater, container, false)
        setupViews()
        viewModel.getFields().observe(viewLifecycleOwner, {fields ->
            Log.d("M_FieldsFragment", "${fields.size}")
            renderUi(fields)
        })
        return binding.root
    }

    private fun setupViews(){
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

        val touchCallback = FieldItemTouchHelperCallback(fieldsAdapter) {
            viewModel.deleteFieldById(it.id)
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        with(binding.rvFields){
            touchHelper.attachToRecyclerView(this)
            adapter = fieldsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(divider)
        }
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.fieldsMapFragment)
        }
    }


    private fun renderUi(fields: List<Field>){
        fieldsAdapter.updateItems(fields)
    }

}