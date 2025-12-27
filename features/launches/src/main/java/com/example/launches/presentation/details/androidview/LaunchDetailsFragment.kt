package com.example.launches.presentation.details.androidview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.launches.R
import com.example.launches.databinding.FragmentLaunchDetailsBinding
import com.example.launches.model.LaunchDetailsIntent
import com.example.launches.model.LaunchDetailsUiState
import com.example.launches.viewmodel.details.LaunchDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LaunchDetailsFragment : Fragment(R.layout.fragment_launch_details) {
    private val viewModel: LaunchDetailsViewModel by viewModels()
    private var binding: FragmentLaunchDetailsBinding? = null // Assumo tu crei l'XML

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLaunchDetailsBinding.bind(view)

        binding?.retryButton?.setOnClickListener {
            viewModel.process(LaunchDetailsIntent.Retry)
        }

        binding?.toolbar?.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: LaunchDetailsUiState) {
        binding?.progressBar?.isVisible = state is LaunchDetailsUiState.Loading
        binding?.contentLayout?.isVisible = state is LaunchDetailsUiState.Success
        binding?.errorLayout?.isVisible = state is LaunchDetailsUiState.Error

        when (state) {
            is LaunchDetailsUiState.Success -> {
                with(binding!!) {
                    missionName.text = state.launch.missionName
                    rocketName.text = state.launch.rocketName
                    launchDate.text = state.launch.launchDate.toString()

                    missionPatch.load(state.launch.patchImageUrl) {
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.placeholder)
                    }

                    if (state.launch.isSuccess) {
                        statusText.text = "Success"
                        statusIcon.setImageResource(R.drawable.ic_check_circle_24)
                    } else {
                        statusText.text = "Failure"
                        statusIcon.setImageResource(R.drawable.ic_warning_24)
                    }
                }
            }

            is LaunchDetailsUiState.Error -> {
                binding?.errorText?.text = state.message
            }

            else -> {}
        }
    }
}
