package ru.netology.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.R
import ru.netology.databinding.FragmentOpenPostBinding
import ru.netology.viewmodel.PostViewModel


class OpenPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

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

        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.openPostById.observe(viewLifecycleOwner) { postId ->
                val post = it.posts?.find { it.id == postId }
                with(binding.scrollPostContent) {
                    post?.let { post ->
                        postsAuthor.text = post.author
                        postsContent.text = post.content
                        postsPublished.text = post.published
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
                                            viewModel.removeById(post.id)
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