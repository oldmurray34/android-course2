package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonLogin.setOnClickListener {
            viewModel.updateUserAuth(
                binding.editLogin.text.toString(),
                binding.editPassword.text.toString()
            )
        }

        viewModel.user.observe(viewLifecycleOwner, { state ->
            if (state.user.id == 0L) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.login_password_error),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                Utils.hideKeyboard(requireView())
            } else {
                AppAuth.getInstance().setAuth(state.user.id, state.user.token)
                findNavController().navigateUp()
                Utils.hideKeyboard(requireView())
            }
        })

        return binding.root
    }
}