package ru.netology.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.R
import ru.netology.databinding.FragmentNewPostBinding
import ru.netology.model.PhotoModel
import ru.netology.util.AndroidUtil
import ru.netology.util.StringArg
import ru.netology.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    requireContext(),
                    "Photo picker error",
                    Toast.LENGTH_SHORT
                ).show()
                Activity.RESULT_OK -> {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    val file = uri?.toFile()
                    viewModel.changePhoto(PhotoModel(uri, file))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtil.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)

        viewModel.photoState.observe(viewLifecycleOwner) { photoModel ->
            if (photoModel == null) {
                binding.photoContainer.isVisible = false
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.preview.setImageURI(photoModel.uri)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
        }

        binding.edit.requestFocus()
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }
        return binding.root
    }
}