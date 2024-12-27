// EventFormFragment.kt
package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityPostEventBinding

import kh.edu.rupp.ite.autumn.ui.viewmodel.EventViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref

class EventFormFragment : Fragment() {

    private lateinit var binding: ActivityPostEventBinding
    private val viewModel by viewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityPostEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setObserver()
    }

    private fun setupListener() {
        binding.btnSubmitEvent.setOnClickListener {
            onEventButtonClick()
        }
    }

    private fun setObserver() {
        viewModel.eventData.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun onEventButtonClick() {
        val date = binding.etDate.text.toString().trim()
        val name = binding.etNameEvent.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val isSpecial = binding.switchIsSpecial.isChecked
        val thumbnail = binding.etThumbnail.text.toString().trim()

        if (date.isEmpty() || name.isEmpty() || time.isEmpty() || description.isEmpty() || thumbnail.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val postEventRequest = PostEventRequest(
            date = date,
            event_info = listOf(
                EventInfo(name, time, description, isSpecial, thumbnail, "")
            )
        )

        val token = AppEncryptedPref.get().getToken(requireContext())

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authorization token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.postEvent(token, postEventRequest)
    }

    private fun handleState(state: ApiState<EventData>) {
        when (state.state) {
            State.loading -> {
                // Show loading UI
            }
            State.success -> {
                Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_SHORT).show()
            }
            State.error -> {
                Toast.makeText(requireContext(), state.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("LogInActivity", "Unhandled state: ${state.state}")
            }
        }
    }
}
