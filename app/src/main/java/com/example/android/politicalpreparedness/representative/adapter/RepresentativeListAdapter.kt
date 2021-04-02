package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresentativesListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

const val REDUCE_ICON_OPACITY = 0.20f

class RepresentativeListAdapter : ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback) {

    companion object RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.office.name == newItem.office.name
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(val binding: RepresentativesListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative) {
        binding.representative = item
        binding.representativeImage.setImageResource(R.drawable.ic_profile)

        item.official.channels?.let { showSocialLinks(it) }
        item.official.urls?.let { showWWWLinks(it) }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RepresentativesListItemBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.representativeFacebook, facebookUrl)
        } else {
            binding.representativeFacebook.alpha = REDUCE_ICON_OPACITY
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.representativeTwitter, twitterUrl)
        } else {
            binding.representativeTwitter.alpha = REDUCE_ICON_OPACITY
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.representativeWww, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}
