package com.mindguard.ai.ui.professionals

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.utils.Resource

class ProfessionalsFragment : Fragment() {

    private val viewModel: ProfessionalsViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        ProfessionalsViewModelFactory(appContainer.professionalRepository)
    }

    private lateinit var professionalsAdapter: ProfessionalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_professionals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val etSearch: TextInputEditText = view.findViewById(R.id.etSearch)
        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroupSpecialties)
        val rvProfessionals: RecyclerView = view.findViewById(R.id.rvProfessionals)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val llEmptyState: LinearLayout = view.findViewById(R.id.llEmptyState)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        professionalsAdapter = ProfessionalsAdapter { professional ->
            val bundle = bundleOf(
                "professionalId" to professional.professionalId,
                "professionalName" to professional.name,
                "professionalTitle" to professional.title,
                "consultationFee" to professional.consultationFee
            )
            findNavController().navigate(R.id.action_professionals_to_detail, bundle)
        }

        rvProfessionals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = professionalsAdapter
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                val specialtyText = chip?.text?.toString() ?: "All"
                viewModel.filterBySpecialty(specialtyText)
            } else {
                viewModel.filterBySpecialty("All")
            }
        }

        viewModel.professionals.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    llEmptyState.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.filteredProfessionals.observe(viewLifecycleOwner) { list ->
            professionalsAdapter.submitList(list)
            llEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
