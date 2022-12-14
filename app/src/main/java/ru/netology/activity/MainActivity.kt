package ru.netology.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import ru.netology.adapter.PostsAdapter
import ru.netology.adapter.onInteractionListener
import ru.netology.databinding.ActivityMainBinding
import ru.netology.dto.Post
import ru.netology.util.AndroidUtil
import ru.netology.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: PostViewModel by viewModels()
    private val adapter = PostsAdapter(object : onInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                binding.canselSaveButton.visibility = View.GONE
                return@observe
            } else {
                binding.canselSaveButton.visibility = View.VISIBLE
            }
            with(binding.contentEditText) {
                requestFocus()
                setText(post.content)
            }
        }

        val itemAnimator = binding.list.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        binding.saveButton.setOnClickListener {
            with(binding.contentEditText) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Content can't be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtil.hideKeyboard(this)
            }
        }

        binding.canselSaveButton.setOnClickListener {
            with(binding.contentEditText) {
                setText("")
                clearFocus()
                AndroidUtil.hideKeyboard(this)
            }
            binding.canselSaveButton.visibility = View.GONE
        }
    }
}