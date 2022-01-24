package ru.netology.nmedia.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentRegBinding
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.AuthViewModel
import java.io.File

class RegFragment : Fragment() {

    private val avatarRequestCode = 1

    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var fragmentBinding: FragmentRegBinding? = null

    private fun onCreateDialog(): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.custom_dialog_registration_done)
        builder.setPositiveButton("Ok") { _: DialogInterface, _: Int ->
        }
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRegBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentBinding = binding

        binding.regButtonAddAvatar.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(192, 192)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .start(avatarRequestCode)
        }

        binding.buttonRegistration.setOnClickListener {

            if (binding.regEditPassword.text.toString() != binding.regConfirmationEditPassword.text.toString()) {
                Toast.makeText(
                    requireContext(),
                    R.string.pass_miss,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (viewModel.avatar.value?.file == null) {
                viewModel.registrationUser(
                    binding.regEditLogin.text.toString(),
                    binding.regEditPassword.text.toString(),
                    binding.regEditName.text.toString()

                )
            } else {
                viewModel.registrationUserWithAvatar(
                    binding.regEditLogin.text.toString(),
                    binding.regEditPassword.text.toString(),
                    binding.regEditName.text.toString()
                )
            }
        }

        viewModel.user.observe(viewLifecycleOwner, { state ->
            if (state.user.id == 0L) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.server_error_string),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                Utils.hideKeyboard(requireView())
            } else {
                onCreateDialog().show()
                AppAuth.getInstance().setAuth(state.user.id, state.user.token)
                findNavController().navigateUp()
                Utils.hideKeyboard(requireView())
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == avatarRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changeAvatar(uri, file)
            return
        }
    }
}