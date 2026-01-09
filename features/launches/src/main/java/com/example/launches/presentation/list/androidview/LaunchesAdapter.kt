package com.example.launches.presentation.list.androidview

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.launches.R
import com.example.launches.databinding.ItemLaunchBinding
import com.example.launches.model.LaunchUiModel

class LaunchesAdapter(
    private val onLaunchClick: (String) -> Unit
) : ListAdapter<LaunchUiModel, LaunchesAdapter.LaunchViewHolder>(LaunchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {
        val binding = ItemLaunchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LaunchViewHolder(binding, onLaunchClick)
    }

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LaunchViewHolder(
        private val binding: ItemLaunchBinding,
        onItemClicked: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentLaunchId: String? = null

        init {
            binding.root.setOnClickListener {
                currentLaunchId?.let { id -> onItemClicked(id) }
            }
        }

        fun bind(launchUiModel: LaunchUiModel) {
            currentLaunchId = launchUiModel.id

            with(binding) {
                missionName.text = launchUiModel.missionName
                rocketName.text = launchUiModel.rocketName
                launchDate.text = launchUiModel.launchDate.toString()

                mainImage.load(launchUiModel.patchImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }

                launchUiModel.isSuccess?.let { isSuccess ->
                    val context = root.context
                    val (iconRes, colorRes) = if (isSuccess) {
                        R.drawable.ic_check_circle_24 to R.color.success
                    } else {
                        R.drawable.ic_warning_24 to R.color.error
                    }

                    statusIcon.setImageResource(iconRes)
                    statusIcon.setColorFilter(
                        ContextCompat.getColor(context, colorRes),
                        PorterDuff.Mode.SRC_IN
                    )
                    statusIcon.visibility = View.VISIBLE
                } ?: {
                    statusIcon.visibility = View.GONE
                }
            }
        }
    }

    class LaunchDiffCallback : DiffUtil.ItemCallback<LaunchUiModel>() {
        override fun areItemsTheSame(oldItem: LaunchUiModel, newItem: LaunchUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LaunchUiModel, newItem: LaunchUiModel): Boolean =
            oldItem == newItem
    }
}
