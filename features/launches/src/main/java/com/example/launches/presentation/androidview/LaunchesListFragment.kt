package com.example.launches.presentation.androidview

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.launches.R
import com.example.launches.databinding.FragmentLaunchesBinding
import com.example.launches.model.LaunchesUiState
import com.example.launches.viewmodel.LaunchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LaunchesListFragment : Fragment(R.layout.fragment_launches) {

    interface OnLaunchClickedListener {
        fun onLaunchClicked()
    }

    private var listener: OnLaunchClickedListener? = null

    private val viewModel: LaunchesViewModel by viewModels()
    private val adapter = LaunchesAdapter { _ ->
        listener?.onLaunchClicked()
    }
    private var binding: FragmentLaunchesBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLaunchClickedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLaunchClickedListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLaunchesBinding.bind(view)

        setupRecyclerView()
        observeState()

        binding?.retryButton?.setOnClickListener {
            viewModel.onRefresh()
        }
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.adapter = adapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: LaunchesUiState) {
        binding?.progressBar?.isVisible = state is LaunchesUiState.Loading
        binding?.errorLayout?.isVisible = state is LaunchesUiState.Error && state.launches.isEmpty()
        binding?.recyclerView?.isVisible =
            state is LaunchesUiState.Success || (state is LaunchesUiState.Error && state.launches.isNotEmpty())

        when (state) {
            is LaunchesUiState.Success -> adapter.submitList(state.launches)
            is LaunchesUiState.Error -> {
                adapter.submitList(state.launches) // Show old data if available
                if (state.launches.isNotEmpty()) {
                    Toast.makeText(context, "Error: ${state.error}", Toast.LENGTH_SHORT).show()
                } else {
                    binding?.errorText?.text = state.error.toString()
                }
            }

            else -> {}
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
