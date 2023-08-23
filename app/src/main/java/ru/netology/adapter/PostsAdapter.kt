package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.dto.Advertising
import ru.netology.dto.FeedItem
import ru.netology.dto.Post
import ru.netology.BuildConfig
import ru.netology.R
import ru.netology.databinding.CardAdvertisingBinding
import ru.netology.databinding.CardPostBinding
import ru.netology.view.load

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
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Advertising -> R.layout.card_advertising
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_advertising -> {
                val binding = CardAdvertisingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AdvertisingViewHolder(binding)
            }

            else -> error("unknown view type: $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Advertising -> (holder as? AdvertisingViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdvertisingViewHolder(
    private val binding: CardAdvertisingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Advertising) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
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

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}