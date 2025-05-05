package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kh.edu.rupp.ite.autumn.databinding.ActivityChatBotBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityPostEventBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.EventViewModel

class ChatBotFragment : BaseFragment() {

    private lateinit var binding: ActivityChatBotBinding
    private val viewModel by viewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityChatBotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupUi()
        setUpListener()
        setObserver()
    }

    private fun setObserver() {
        // Placeholder: Add real LiveData observer logic here later
        // Example: viewModel.someData.observe(viewLifecycleOwner) { /* handle data */ }
        println("Observer set")
    }

    private fun setUpListener() {
        // Placeholder: Add real listener logic here later
        // Example: binding.sendButton.setOnClickListener { sendMessage() }
        println("Listener set")
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack() // Go back to HomeFragment
        }
    }

}