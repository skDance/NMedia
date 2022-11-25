package ru.netology

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.databinding.ActivityMainBinding
import ru.netology.dto.Post
import ru.netology.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                postsAuthor.text = post.author
                postsContent.text = post.content
                postsPublished.text = post.published
                likesCount.text = post.setCount(post.likesCount)
                sharesCount.text = post.setCount(post.sharesCount)
                viewsCount.text = post.setCount(post.viewsCount)
                likesButton.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
            }
        }

        binding.likesButton.setOnClickListener {
            viewModel.like()
        }
        binding.shareButton.setOnClickListener {
            viewModel.share()
        }
    }
}