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
        TODO("Not yet implemented")
    }

    private fun setUpListener() {
        TODO("Not yet implemented")
    }
}