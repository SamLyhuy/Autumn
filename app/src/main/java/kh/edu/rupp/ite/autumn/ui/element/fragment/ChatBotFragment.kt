package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.databinding.ActivityChatBotBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.ChatViewModel
import kh.edu.rupp.ite.autumn.data.model.ChatMessage
import kh.edu.rupp.ite.autumn.data.model.ChatRequest
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.ui.element.activity.MainActivity
import kh.edu.rupp.ite.autumn.ui.element.adapter.ChatAdapter

class ChatBotFragment : BaseFragment() {

    private val WAITING_TEXT = "Please wait, fetching your results…"
    private val TYPING_MSG = ChatMessage(isUser = false, isTyping = true)
    private var _binding: ActivityChatBotBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    companion object {
        private const val TAG = "ChatBotFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityChatBotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: setting up UI components")
        setupRecyclerView()
        setupObserver()
        setupListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: initializing adapter and layout manager")
        adapter = ChatAdapter(messages)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = this@ChatBotFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservera() {
        Log.d(TAG, "setupObserver: subscribing to chatReply LiveData")
        viewModel.chatReply.observe(viewLifecycleOwner) { apiState ->
            when (apiState.state) {
                State.loading -> {
                    Log.d(TAG, "State.loading: waiting for response")
                }

                State.success -> {
                    // 1) Mark where this block starts
                    val startIndex = messages.size

                    // 2) Add the AI's text reply (if any)
                    apiState.message
                        ?.takeIf { it.isNotBlank() }
                        ?.let { botText ->
                            Log.d(TAG, "Adding bot text: $botText")
                            messages.add(ChatMessage(text = botText, isUser = false))
                        }

                    // 3) Add a single horizontal block containing all FoodData
                    apiState.data
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { foodList ->
                            Log.d(TAG, "Adding food list block of size: ${foodList.size}")
                            messages.add(ChatMessage(isUser = false, foodList = foodList))
                        }

                    // 4) Notify the adapter of the inserted range
                    val insertedCount = messages.size - startIndex
                    adapter.notifyItemRangeInserted(startIndex, insertedCount)

                    // 5) Scroll so that the bot text bubble is visible
                    binding.chatRecyclerView.scrollToPosition(startIndex)
                }

                State.error -> {
                    val err = apiState.message ?: "Unknown error"
                    Log.e(TAG, "State.error: $err")
                    messages.add(ChatMessage(text = "Error: $err", isUser = false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                }

                else -> { /* no-op */ }
            }
        }
    }

    private fun setupObserver() {
        viewModel.chatReply.observe(viewLifecycleOwner) { apiState ->
            when (apiState.state) {
                State.loading -> {
                    // only add one waiting bubble
//                    if (messages.lastOrNull()?.text != WAITING_TEXT) {
//                        messages.add(ChatMessage(text = WAITING_TEXT, isUser = false))
//                        adapter.notifyItemInserted(messages.size - 1)
//                        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
//                    }
                    // show typing once
                    if (messages.lastOrNull() != TYPING_MSG) {
                        messages.add(TYPING_MSG)
                        adapter.notifyItemInserted(messages.size - 1)
                        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                    }
                }

                State.success -> {
                    // remove any waiting bubble
//                    val removed = messages.removeAll { it.text == WAITING_TEXT && !it.isUser }
//                    if (removed) adapter.notifyDataSetChanged()

                    if (messages.remove(TYPING_MSG)) {
                        adapter.notifyDataSetChanged()
                    }

                    // now add the real response
                    val start = messages.size

                    apiState.message
                        ?.takeIf(String::isNotBlank)
                        ?.let { messages.add(ChatMessage(text = it, isUser = false)) }

                    apiState.data
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { messages.add(ChatMessage(isUser = false, foodList = it)) }

                    adapter.notifyItemRangeInserted(start, messages.size - start)
                    binding.chatRecyclerView.scrollToPosition(start)
                }

                State.error -> {
                    // remove wait bubble if present
//                    if (messages.removeAll { it.text == WAITING_TEXT && !it.isUser }) {
//                        adapter.notifyDataSetChanged()
//                    }
                    if (messages.remove(TYPING_MSG)) {
                        adapter.notifyDataSetChanged()
                    }
                    val err = apiState.message ?: "Unknown error"
                    messages.add(ChatMessage(text = "Error: $err", isUser = false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                }

                else -> {}
            }
        }
    }


    private fun setupListener() {
        Log.d(TAG, "setupListener: attaching click listeners")
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
            (requireActivity() as? MainActivity)?.closeChat()
        }
        binding.sendButton.setOnClickListener {
            val msg = binding.messageInput.text.toString().trim()
            if (msg.isEmpty()) return@setOnClickListener

            binding.messageInput.text?.clear()
            messages.add(ChatMessage(text = msg, isUser = true))
            adapter.notifyItemInserted(messages.size - 1)
            binding.chatRecyclerView.scrollToPosition(messages.size - 1)

            Log.d(TAG, "Sending message to ViewModel: '$msg'")
            viewModel.sendMessage(ChatRequest(message = msg))
        }
    }
}
