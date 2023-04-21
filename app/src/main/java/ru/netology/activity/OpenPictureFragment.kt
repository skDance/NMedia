package ru.netology.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import ru.netology.R
import ru.netology.databinding.FragmentOpenPictureBinding
import ru.netology.viewmodel.PostViewModel


class OpenPictureFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOpenPictureBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.openPicture.observe(viewLifecycleOwner) {
            Glide.with(binding.picture)
                .load("http://10.0.2.2:10999/media/${viewModel.openPicture.value}")
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_image_error)
                .timeout(10_000)
                .into(binding.picture)
        }
        return binding.root
    }
}