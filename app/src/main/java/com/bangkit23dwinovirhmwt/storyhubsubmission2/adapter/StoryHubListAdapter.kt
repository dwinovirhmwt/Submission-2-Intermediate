package com.bangkit23dwinovirhmwt.storyhubsubmission2.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ListStoryItem
import com.bangkit23dwinovirhmwt.storyhubsubmission2.databinding.ListItemsBinding
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.detail.DetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class StoryHubListAdapter : PagingDataAdapter<ListStoryItem, StoryHubListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ListItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.apply {
                Glide.with(itemView.context).load(data.photoUrl)
                    .transform(CenterCrop(), RoundedCorners(30)).into(ivImage)
                tvName.text = data.name
                tvDesc.text = data.description
            }

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivImage, "photo"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDesc, "description")
                    )

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("id", data.id)
                Log.d("id", data.id)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}