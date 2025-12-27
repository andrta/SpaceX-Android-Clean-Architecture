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
import com.example.launches.model.LaunchesIntent
import com.example.launches.model.LaunchesUiEffect
import com.example.launches.model.LaunchesUiState
import com.example.launches.viewmodel.LaunchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LaunchesFragment : Fragment(R.layout.fragment_launches) {
    interface NavigationListener {
        fun onNavigateToDetail(id: String)
    }

    private var navigationListener: NavigationListener? = null
    private val viewModel: LaunchesViewModel by viewModels()
    private val adapter = LaunchesAdapter { launchId ->
        viewModel.process(LaunchesIntent.LaunchClicked(launchId))
    }

    private var binding: FragmentLaunchesBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationListener) {
            navigationListener = context
        } else {
            throw RuntimeException("$context must implement NavigationListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLaunchesBinding.bind(view)

        setupRecyclerView()
        setupIntents()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding?.recyclerView?.adapter = adapter
    }

    private fun setupIntents() {
        binding?.retryButton?.setOnClickListener {
            viewModel.process(LaunchesIntent.Refresh)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }
                launch {
                    viewModel.uiEffect.collect { effect ->
                        handleEffect(effect)
                    }
                }
            }
        }
    }

    private fun renderState(state: LaunchesUiState) {
        binding?.progressBar?.isVisible = state is LaunchesUiState.Loading

        val isListEmpty = (state is LaunchesUiState.Error && state.launches.isEmpty())
        binding?.errorLayout?.isVisible = isListEmpty

        val hasData =
            (state is LaunchesUiState.Success) || (state is LaunchesUiState.Error && state.launches.isNotEmpty())
        binding?.recyclerView?.isVisible = hasData

        when (state) {
            is LaunchesUiState.Success -> {
                adapter.submitList(state.launches)
            }

            is LaunchesUiState.Error -> {
                adapter.submitList(state.launches) // Preserviamo i dati vecchi

                if (isListEmpty) {
                    binding?.errorText?.text = state.error.toString()
                }
            }

            else -> {}
        }
    }

    private fun handleEffect(effect: LaunchesUiEffect) {
        when (effect) {
            is LaunchesUiEffect.NavigateToDetail -> {
                navigationListener?.onNavigateToDetail(effect.launchId)
            }

            is LaunchesUiEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
