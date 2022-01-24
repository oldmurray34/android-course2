package ru.netology.nmedia.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.CardViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.File


class NewPostFragment : Fragment() {

    private val photoRequestCode = 1
    private val cameraRequestCode = 2

    companion object {
        var Bundle.textArg: String? by Utils.StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val cardPostViewModel: CardViewModel by viewModels()

    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs: SharedPreferences? = this.context?.getSharedPreferences("draft", MODE_PRIVATE)
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    viewModel.changeContent(0, it.etInputArea.text.toString())
                    viewModel.postCreation()
                    Utils.hideKeyboard(requireView())
                    if (prefs != null) {
                        clearDraft(prefs)
                    }
                }
                true
            }
            R.id.signOut -> {
                fragmentBinding?.let {
                    onCreateDialog().show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onCreateDialog(): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.custom_dialog_exit_warning)
        builder.setIcon(R.drawable.ic_baseline_logout_24)
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            AppAuth.getInstance().removeAuth()
            findNavController().navigateUp()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        this.activity?.title = "Post creation"

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentBinding = binding

        val prefs: SharedPreferences? = this.context?.getSharedPreferences("draft", MODE_PRIVATE)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (prefs != null) {
                saveDraft(prefs, binding.etInputArea.text.toString())
                this@NewPostFragment.onDetach()
                findNavController().navigateUp()
            }
        }

        restoreDraft(prefs, binding)

        arguments?.textArg
            ?.let(binding.etInputArea::setText)

        binding.etInputArea.requestFocus()
        callback.isEnabled

        cardPostViewModel.post.observe(viewLifecycleOwner, { state ->
            if (state.error) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_loading,
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                viewModel.postCreated.observe(viewLifecycleOwner) {
                    viewModel.loadPosts()
                    findNavController().navigateUp()
                }
            }
        })

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

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
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}

private fun restoreDraft(
    prefs: SharedPreferences?,
    binding: FragmentNewPostBinding
) {
    val draftText = prefs?.getString("draftText", "")

    if (draftText != "") {
        binding.etInputArea.setText(draftText)
    }
}

private fun saveDraft(prefs: SharedPreferences, text: String) {
    val editor = prefs.edit()
    editor.putString("draftText", text)
    editor.apply()
}

private fun clearDraft(prefs: SharedPreferences) {
    val editor = prefs.edit()
    editor.remove("draftText")
    editor.apply()
}

