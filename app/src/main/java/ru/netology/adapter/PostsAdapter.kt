package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.R
import ru.netology.databinding.CardPostBinding
import ru.netology.dto.Post

interface onInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun openPost(post: Post) {}
    fun openPicture(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: onInteractionListener
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ru.netology.databinding.CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: onInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    val dateFormat = java.text.SimpleDateFormat("HH:mm dd.MM.yyyy")
    fun bind(post: Post) {
        binding.apply {
            postsAuthor.text = post.author
            postsContent.text = post.content
            postsPublished.text = dateFormat.format(java.util.Date(post.published.toLong() * 1000))
            likesButton.text = post.setCount(post.likesCount)
            shareButton.text = post.setCount(post.sharesCount)
            viewsButton.text = post.setCount(post.viewsCount)
            likesButton.isChecked = post.likedByMe
            likesButton.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareButton.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            menuButton.isVisible = post.ownedByMe
            menuButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            videoFrame.isVisible = false

            videoFrame.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            cardPost.setOnClickListener {
                onInteractionListener.openPost(post)
            }

            attachmentImage.setOnClickListener {
                onInteractionListener.openPicture(post)
            }

            Glide.with(postAvatar)
                .load("http://10.0.2.2:10999/avatars/${post.authorAvatar}")
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_image_error)
                .circleCrop()
                .timeout(10_000)
                .into(postAvatar)

            if (post.attachment == null) {
                attachmentImage.isVisible = false
            } else {
                attachmentImage.isVisible = true
                Glide.with(attachmentImage)
                    .load("http://10.0.2.2:10999/media/${post.attachment.url}")
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_image_error)
                    .timeout(10_000)
                    .into(attachmentImage)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}