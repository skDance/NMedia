package ru.netology.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.R
import ru.netology.databinding.FragmentOpenPostBinding
import ru.netology.viewmodel.PostViewModel

@AndroidEntryPoint
class OpenPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOpenPostBinding.inflate(
            inflater,
            container,
            false
        )

        val dateFormat = java.text.SimpleDateFormat("HH:mm dd.MM.yyyy")

        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.openPostById.observe(viewLifecycleOwner) { postId ->
                val post = it.posts?.find { it.id == postId }
                with(binding.scrollPostContent) {
                    post?.let { post ->
                        postsAuthor.text = post.author
                        postsContent.text = post.content
                        postsPublished.text =
                            dateFormat.format(java.util.Date(post.published.toLong() * 1000))
                        likesButton.text = post.setCount(post.likesCount)
                        shareButton.text = post.setCount(post.sharesCount)
                        viewsButton.text = post.setCount(post.viewsCount)
                        likesButton.isChecked = post.likedByMe

                        likesButton.setOnClickListener {
                            viewModel.likeById(post)
                        }
                        shareButton.setOnClickListener {
                            viewModel.shareById(post.id)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, post.content)
                                type = "text/plain"
                            }
                            val shareIntent =
                                Intent.createChooser(intent, getString(R.string.chooser_share_post))
                            startActivity(shareIntent)
                        }
                        menuButton.setOnClickListener { it ->
                            PopupMenu(it.context, it).apply {
                                inflate(R.menu.options_post)
                                setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.remove -> {
                                            findNavController().navigateUp()
                                            viewModel.removeById(post)
                                            true
                                        }
                                        R.id.edit -> {
                                            viewModel.edit(post)
                                            val text = post.content
                                            val bundle = Bundle()
                                            bundle.putString("editPostText", text)
                                            findNavController().navigate(
                                                R.id.action_openPostFragment_to_editPostFragment,
                                                bundle
                                            )
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }.show()
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
                                .load("http://10.0.2.2:10999/images/${post.attachment.url}")
                                .placeholder(R.drawable.ic_avatar_placeholder)
                                .error(R.drawable.ic_image_error)
                                .timeout(10_000)
                                .into(attachmentImage)
                        }

                        videoFrame.isVisible = false

//                        videoFrame.setOnClickListener {
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
//                            startActivity(intent)
//                        }
                    }
                }
            }
        }
        return binding.root
    }
}