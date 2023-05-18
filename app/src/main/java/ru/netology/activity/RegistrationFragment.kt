package ru.netology.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.R
import ru.netology.databinding.FragmentRegistrationBinding
import ru.netology.util.AndroidUtil
import ru.netology.viewmodel.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val regViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegistrationBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            regButton.setOnClickListener {
                when {
                    regLogin.text.isNullOrBlank() || regPassword.text.isNullOrBlank() -> Toast.makeText(
                        activity,
                        R.string.error_signin_log_or_pas,
                        Toast.LENGTH_LONG
                    ).show()
                    regPasswordConfirm.text.isNullOrBlank() -> Toast.makeText(
                        activity,
                        R.string.reg_error_password_confirm_null,
                        Toast.LENGTH_LONG
                    ).show()
                    regPassword.text.toString() != regPasswordConfirm.text.toString() -> Toast.makeText(
                        activity,
                        R.string.reg_error_password_confirm,
                        Toast.LENGTH_LONG
                    ).show()
                    else -> {
                        regViewModel.registration(
                            regLogin.text.toString(),
                            regPassword.text.toString(),
                            regName.text.toString()
                        )
                        AndroidUtil.hideKeyboard(requireView())
                    }
                }
            }
        }

        regViewModel.registered.observe(viewLifecycleOwner) { state ->
            binding.regProgress.isVisible = state.authLoading
            if (state.authSuccessful) {
                findNavController().navigate(R.id.action_registrationFragment_to_feedFragment)
            } else {
                Toast.makeText(activity, R.string.error_signin, Toast.LENGTH_LONG).show()
                binding.regPassword.text.clear()
                binding.regPasswordConfirm.text.clear()
            }
        }

        return binding.root
    }
}