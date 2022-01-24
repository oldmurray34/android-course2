package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnItemClickListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                findNavController().navigate(R.id.action_mainFragment_to_authFragment)
                true
            }
            R.id.signup -> {
                findNavController().navigate(R.id.action_mainFragment_to_regFragment)
                true
            }

            R.id.signout -> {
                AppAuth.getInstance().removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.activity?.title = "NMedia"

        val binding = FragmentMainBinding.inflate(
            inflater,
            container,
            false
        )

        binding.swiperefresh.setOnRefreshListener {
            if (!binding.progress.isAnimating) {
                viewModel.loadPosts()
            }
            binding.swiperefresh.isRefreshing = false
        }

        val adapter = PostAdapter(object : OnItemClickListener {

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likeByMe) {
                        viewModel.likeById(post.id)

                    } else {
                        viewModel.unlikeById(post.id)
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.authorization_required),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.authorize)) {
                            findNavController().navigate(R.id.action_mainFragment_to_authFragment)
                        }
                        .show()
                    returnTransition
                }
            }

            override fun onEdit(post: Post) {
                val bundle = Bundle().apply {
                    putString("content", post.content)
                    putLong("postId", post.id)
                }
                findNavController().navigate(R.id.action_mainFragment_to_editPostFragment, bundle)
            }

            override fun onDelete(post: Post) {
                viewModel.deleteById(post.id)
            }

            override fun onPost(post: Post) {
                val bundle = Bundle().apply {
                    putLong("postId", post.id)
                    putLong("authorId", post.authorId)
                    putString("author", post.author)
                    putString("authorAvatar", post.authorAvatar)
                    putString("content", post.content)
                    putString("published", post.published)
                    putBoolean("likeByMe", post.likeByMe)
                    putInt("numberOfLikes", post.numberOfLikes)
                    if (post.attachment != null) {
                        putString("attachmentUrl", post.attachment.url)
                        putString("attachmentType", post.attachment.type.toString())
                    }
                }
                findNavController().navigate(R.id.action_mainFragment_to_postCardFragment, bundle)
            }

            override fun onPhoto(post: Post) {
                val bundle = Bundle().apply {
                    putLong("postId", post.id)
                    putLong("authorId", post.authorId)
                    putString("author", post.author)
                    putString("authorAvatar", post.authorAvatar)
                    putString("content", post.content)
                    putString("published", post.published)
                    putBoolean("likeByMe", post.likeByMe)
                    putInt("numberOfLikes", post.numberOfLikes)
                    if (post.attachment != null) {
                        putString("attachmentUrl", post.attachment.url)
                        putString("attachmentType", post.attachment.type.toString())
                    }
                }
                findNavController().navigate(R.id.action_mainFragment_to_photoFragment, bundle)
            }
        })

        binding.rvPosts.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        })

        viewModel.newerCount.observe(viewLifecycleOwner, { state ->
            when {
                state != 0 -> binding.newPostsChip.visibility = View.VISIBLE
                else -> binding.newPostsChip.visibility = View.GONE
            }
        })

        binding.newPostsChip.setOnClickListener {
            viewModel.run {
                makeReadPosts()
                loadPosts()
            }
            binding.rvPosts.smoothSnapToPosition(0)
            it.visibility = View.GONE
        }

        binding.fabAddNewPost.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.action_mainFragment_to_newPostFragment)
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.authorization_required),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.authorize)) {
                        findNavController().navigate(R.id.action_mainFragment_to_authFragment)
                    }
                    .show()
            }
        }
        return binding.root
    }

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_START
    ) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }
}