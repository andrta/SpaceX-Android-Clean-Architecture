package com.example.launches.presentation.androidview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.domain.models.Launch
import com.example.launches.R
import com.example.launches.databinding.ItemLaunchBinding

class LaunchesAdapter(
    private val onLaunchClick: (String) -> Unit
) : ListAdapter<Launch, LaunchesAdapter.LaunchViewHolder>(LaunchDiffCallback()) {

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

        fun bind(launch: Launch) {
            currentLaunchId = launch.id

            with(binding) {
                missionName.text = launch.missionName
                rocketName.text = launch.rocketName
                launchDate.text = launch.launchDate.toString()

                mainImage.load(launch.patchImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }

                val context = root.context
                val (iconRes, colorRes) = if (launch.isSuccess) {
                    R.drawable.ic_check_circle_24 to R.color.success
                } else {
                    R.drawable.ic_warning_24 to R.color.error
                }

                statusIcon.setImageResource(iconRes)
                statusIcon.setColorFilter(
                    ContextCompat.getColor(context, colorRes),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    class LaunchDiffCallback : DiffUtil.ItemCallback<Launch>() {
        override fun areItemsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem == newItem
    }
}
