package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.databinding.ActivityChatBotBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.ChatViewModel
import kh.edu.rupp.ite.autumn.data.model.ChatMessage
import kh.edu.rupp.ite.autumn.data.model.ChatRequest
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.ui.element.adapter.ChatAdapter


class ChatBotFragment : BaseFragment() {
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
        Log.d(TAG, "onCreateView: inflating layout")
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
        Log.d(TAG, "onDestroyView: clearing binding")
        _binding = null
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: initializing adapter and layout manager")
        adapter = ChatAdapter(messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter
    }

    private fun setupObserver() {
        Log.d(TAG, "setupObserver: subscribing to chatReply LiveData")
        viewModel.chatReply.observe(viewLifecycleOwner, Observer { apiState ->
            Log.d(TAG, "chatReply observed: state=${apiState.state}, dataSize=${apiState.data?.size}")
            when (apiState.state) {
                State.loading -> Log.d(TAG, "State.loading: waiting for response")
                State.success -> {
                    val foodList = apiState.data ?: emptyList()
                    foodList.forEach { food ->
                        Log.d(TAG, "Received FoodData thumbnail=${food.thumbnail}")
                        val text = "${food.name} - $${food.price}"
                        messages.add(ChatMessage(text = text, isUser = false, imageUrl = food.thumbnail))
                        adapter.notifyItemInserted(messages.size - 1)
                    }
                    binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                }
                State.error -> {
                    val errorMsg = apiState.message ?: "Unknown error"
                    Log.e(TAG, "State.error: $errorMsg")
                    messages.add(ChatMessage(text = "Error: $errorMsg", isUser = false))
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                }
                else -> Log.w(TAG, "Unhandled state: ${apiState.state}")
            }
        })
    }

    private fun setupListener() {
        Log.d(TAG, "setupListener: attaching click listeners")
        binding.toolbar.setNavigationOnClickListener {
            Log.d(TAG, "Navigation icon clicked: popping back stack")
            parentFragmentManager.popBackStack()
        }
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            Log.d(TAG, "Send button clicked with text: '$messageText'")
            if (messageText.isEmpty()) {
                Log.d(TAG, "Message is empty, ignoring send")
                return@setOnClickListener
            }
            binding.messageInput.text?.clear()
            messages.add(ChatMessage(text = messageText, isUser = true))
            adapter.notifyItemInserted(messages.size - 1)
            binding.chatRecyclerView.scrollToPosition(messages.size - 1)
            Log.d(TAG, "Sending message to ViewModel: '$messageText'")
            viewModel.sendMessage(ChatRequest(message = messageText))
        }
    }
}