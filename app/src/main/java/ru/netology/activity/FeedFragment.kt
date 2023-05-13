package ru.netology.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.launch
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netology.R
import ru.netology.adapter.PostsAdapter
import ru.netology.adapter.onInteractionListener
import ru.netology.auth.AppAuth
import ru.netology.databinding.FragmentFeedBinding
import ru.netology.dto.Post
import ru.netology.viewmodel.AuthViewModel
import ru.netology.viewmodel.PostViewModel

private const val SING_IN = "signIn"
private const val SING_OUT = "signOut"

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val authViewModel: AuthViewModel by viewModels()


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
                viewModel.openPostById.value = post.id
                findNavController().navigate(R.id.action_feedFragment_to_openPostFragment)
            }

            override fun onLike(post: Post) {
                if (!authViewModel.authorized) showDialog(SING_IN) else viewModel.likeById(post)
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
                viewModel.removeById(post)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val text = post.content
                val bundle = Bundle()
                bundle.putString("editPostText", text)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun openPicture(post: Post) {
                viewModel.openPicture.value = post.attachment?.url
                findNavController().navigate(R.id.action_feedFragment_to_openPictureFragment)
            }

//            override fun onPlayVideo(post: Post) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
//                startActivity(intent)
//            }
        })

        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.posts)
            binding.emptyText.isVisible = data.empty
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            println("Newer count: $it")
            if (it == 1) {
                binding.recentEntries.isVisible = true
            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        val itemAnimator = binding.list.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.refreshPosts() }

        binding.fab.setOnClickListener {
            if (!authViewModel.authorized) {
                showDialog(SING_IN)
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        binding.recentEntries.setOnClickListener {
            viewModel.showRecentEntries()
            binding.recentEntries.isVisible = false
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPosts()
            binding.swipeRefresh.isRefreshing = false
        }

        var menuProvider: MenuProvider? = null

        authViewModel.state.observe(viewLifecycleOwner) { authState ->
            menuProvider?.let { requireActivity().removeMenuProvider(it) }

            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_manu, menu)

                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.signout -> {
                            showDialog(SING_OUT)
                            true
                        }
                        R.id.signin -> {
                            findNavController().navigate(R.id.action_feedFragment_to_authorizationFragment)
                            true
                        }
                        R.id.signup -> {
                            findNavController().navigate(R.id.action_feedFragment_to_registrationFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.apply { menuProvider = this }, viewLifecycleOwner)
        }

        return binding.root
    }

    fun showDialog(action: String) {
        val dialog = AlertDialog.Builder(context)
        when (action) {
            SING_IN -> {
                dialog
                    .setTitle(R.string.dialog_auth_title)
                    .setMessage(R.string.dialog_auth_text)
                    .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                        findNavController().navigate(R.id.action_feedFragment_to_authorizationFragment)
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
            }
            SING_OUT -> {
                dialog
                    .setTitle(R.string.dialog_logout_title)
                    .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                        AppAuth.getInstance().clear()
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
            }
        }

        dialog.show()
    }
}

