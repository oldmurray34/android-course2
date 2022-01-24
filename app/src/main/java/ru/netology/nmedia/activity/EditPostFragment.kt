package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.CardViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val cardPostViewModel: CardViewModel by viewModels()

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

        val textForEdit = arguments?.getString("content").toString()
        val postId = arguments?.getLong("postId")

        binding.etInputArea.setText(textForEdit)

        binding.etInputArea.requestFocus()

        binding.fabConfirmation.setOnClickListener {
            if (binding.etInputArea.text.isNullOrBlank()) {
                Utils.hideKeyboard(requireView())
                findNavController().navigateUp()
                return@setOnClickListener
            }

            val content = binding.etInputArea.text.toString()
            viewModel.changeContent(postId!!, content)
            viewModel.postCreation()

            Utils.hideKeyboard(requireView())

            serverErrorHandler()
        }

        binding.mbCancelEditing.setOnClickListener {
            Utils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun serverErrorHandler() {
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
    }

}