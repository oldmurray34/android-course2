package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.CardViewModel
import ru.netology.nmedia.viewmodel.PhotoViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.viewmodel.AuthViewModel


class PhotoFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val cardPostViewModel: CardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val post = Post(
            id = arguments?.getLong("postId") as Long,
            authorId = arguments?.getLong("authorId") as Long,
            author = arguments?.getString("author") as String,
            authorAvatar = arguments?.getString("authorAvatar") as String,
            content = arguments?.getString("content") as String,
            published = arguments?.getString("published") as String,
            likeByMe = arguments?.getBoolean("likeByMe") as Boolean,
            numberOfLikes = arguments?.getInt("numberOfLikes") as Int,
            attachment = if (arguments?.getString("attachmentUrl") == null) {
                null
            } else {
                Attachment(
                    url = arguments?.getString("attachmentUrl") as String,
                    type = enumValueOf<AttachmentType>(
                        arguments?.getString("attachmentType")!!
                    )
                )
            }
        )

        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        binding.apply {

            val urlAttach = "http://10.0.2.2:9999/media/${post.attachment?.url}"

            likeButtonPhotoFragment.text = Utils.formatLikes(post.numberOfLikes)
            likeButtonChange(post)

            if (post.attachment != null) {
                binding.photoView.visibility = View.VISIBLE
                Glide.with(binding.photoView.context)
                    .load(urlAttach)
                    .timeout(30_000)
                    .into(binding.photoView)
            }

            binding.likeButtonPhotoFragment.setOnClickListener {
                if (!post.likeByMe && authViewModel.authenticated) {
                    cardPostViewModel.likeById(post.id)
                } else if (post.likeByMe && authViewModel.authenticated) {
                    cardPostViewModel.unlikeById(post.id)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.authorization_required),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.authorize)) {
                            findNavController().navigate(R.id.action_photoFragment_to_authFragment)
                        }
                        .show()
                    returnTransition
                }
                cardPostViewModel.post.observe(owner = viewLifecycleOwner) {
                    val newPost = it.post
                    binding.likeButtonPhotoFragment.text = Utils.formatLikes(newPost.numberOfLikes)
                    likeButtonChange(newPost)
                }
            }

            binding.mbBackFromPhotoFragment.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    private fun FragmentPhotoBinding.likeButtonChange(post: Post) {
        if (post.likeByMe) {
            likeButtonPhotoFragment.setIconResource(R.drawable.ic_baseline_favorite_24)
            likeButtonPhotoFragment.setIconTintResource(R.color.colorRed)
        } else {
            likeButtonPhotoFragment.setIconResource(R.drawable.ic_baseline_favorite_border_24)
            likeButtonPhotoFragment.setIconTintResource(R.color.colorDarkGrey)
        }
    }
}
