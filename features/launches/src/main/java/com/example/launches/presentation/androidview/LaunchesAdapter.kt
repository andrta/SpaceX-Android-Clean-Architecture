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

class LaunchesAdapter(private val onLaunchClick: (Launch) -> Unit) :
    ListAdapter<Launch, LaunchesAdapter.LaunchViewHolder>(LaunchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {
        val binding = ItemLaunchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaunchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        val launch = getItem(position)
        holder.itemView.setOnClickListener {
            onLaunchClick(launch)
        }
        holder.bind(launch)
    }

    class LaunchViewHolder(private val binding: ItemLaunchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(launch: Launch) {
            binding.missionName.text = launch.missionName
            binding.rocketName.text = launch.rocketName
            binding.launchDate.text = launch.launchDate.toString()

            binding.mainImage.load(launch.patchImageUrl) {
                placeholder(R.drawable.placeholder)
                fallback(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }

            val statusIconRes = if (launch.isSuccess) R.drawable.ic_check_circle_24 else R.drawable.ic_warning_24
            val statusColorRes = if (launch.isSuccess) R.color.success else R.color.error
            binding.statusIcon.setImageResource(statusIconRes)
            binding.statusIcon.setColorFilter(
                ContextCompat.getColor(binding.root.context, statusColorRes),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    class LaunchDiffCallback : DiffUtil.ItemCallback<Launch>() {
        override fun areItemsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Launch, newItem: Launch): Boolean =
            oldItem == newItem
    }
}
