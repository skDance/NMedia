package ru.netology.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.R
import ru.netology.databinding.FragmentEditPostBinding
import ru.netology.util.AndroidUtil
import ru.netology.util.StringArg
import ru.netology.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    companion object {
        var Bundle.edit: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.edit?.let(binding.editText::setText)
        binding.editText.setText(arguments?.getString("editPostText"))

        binding.okEditButton.setOnClickListener {
            if (binding.editText.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeContent(binding.editText.text.toString())
                viewModel.save()
                AndroidUtil.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }
        binding.canselEditButton.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}