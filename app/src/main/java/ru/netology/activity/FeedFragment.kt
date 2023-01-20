package ru.netology.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import ru.netology.R
import ru.netology.adapter.PostsAdapter
import ru.netology.adapter.onInteractionListener
import ru.netology.databinding.FragmentFeedBinding
import ru.netology.dto.Post
import ru.netology.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : onInteractionListener {
            override fun openPost(post: Post) {
                viewModel.openPost.value = post
                findNavController().navigate(R.id.action_feedFragment_to_openPostFragment)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
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

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val text = post.content
                val bundle = Bundle()
                bundle.putString("editPostText", text)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun onPlayVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                startActivity(intent)
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        val itemAnimator = binding.list.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }

}