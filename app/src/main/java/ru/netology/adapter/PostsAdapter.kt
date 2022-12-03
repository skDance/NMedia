package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.R
import ru.netology.databinding.CardPostBinding
import ru.netology.dto.Post

typealias onLikeListener = (post: Post) -> Unit
typealias onShareListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: onLikeListener,
    private val onShareListener: onShareListener
): ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder (
    private val binding: CardPostBinding,
    private val onLikeListener: onLikeListener,
    private val onShareListener: onShareListener
    ): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            postsAuthor.text = post.author
            postsContent.text = post.content
            postsPublished.text = post.published
            likesCount.text = post.setCount(post.likesCount)
            sharesCount.text = post.setCount(post.sharesCount)
            viewsCount.text = post.setCount(post.viewsCount)
            likesButton.setImageResource(
                if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
            )
            likesButton.setOnClickListener {
                onLikeListener(post)
            }
            shareButton.setOnClickListener {
                onShareListener(post)
            }
        }
    }
}

class PostDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}