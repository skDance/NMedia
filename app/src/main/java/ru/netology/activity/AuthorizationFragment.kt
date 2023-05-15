package ru.netology.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.R
import ru.netology.databinding.FragmentAuthorizationBinding
import ru.netology.util.AndroidUtil
import ru.netology.viewmodel.SignInViewModel

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {


    private val signInViewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthorizationBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            authButton.setOnClickListener {
                if (login.text.isNullOrBlank() || password.text.isNullOrBlank()) {
                    Toast.makeText(activity, R.string.error_signin_log_or_pas, Toast.LENGTH_LONG).show()
                } else {
                    signInViewModel.signIn(binding.login.text.toString(), binding.password.text.toString())
                    AndroidUtil.hideKeyboard(requireView())
                }
            }
        }

        signInViewModel.authorized.observe(viewLifecycleOwner) {state ->
            binding.authProgress.isVisible = state.authLoading
            if (state.authSuccessful) {
                findNavController().navigate(R.id.action_authorizationFragment_to_feedFragment)
            } else {
                Toast.makeText(activity, R.string.error_signin, Toast.LENGTH_LONG).show()
                binding.password.text.clear()
            }
        }

        return binding.root
    }
}