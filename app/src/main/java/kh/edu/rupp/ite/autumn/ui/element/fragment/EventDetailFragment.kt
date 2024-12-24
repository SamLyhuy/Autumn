package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.databinding.DetailEventBinding

class EventDetailFragment :BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailEventBinding.inflate(inflater, container, false)
        val eventData = arguments?.getParcelable<EventData>("event_data")

        binding.textEventName.text = eventData?.name
        binding.textEventDescription.text = eventData?.description

        return binding.root
    }
}