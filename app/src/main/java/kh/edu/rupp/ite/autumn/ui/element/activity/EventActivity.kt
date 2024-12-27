package kh.edu.rupp.ite.autumn.ui.element.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityEventBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityPostEventBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.EventViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref

class EventActivity: BaseActivity() {

    private lateinit var binding: ActivityPostEventBinding
    private val viewModel by viewModels<EventViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        setupListener()
        setObserver()
    }

    private fun setupUi() {
        binding = ActivityPostEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupListener() {
        binding.btnSubmitEvent.setOnClickListener {
            onEventButtonClick()
        }
    }

    private fun setObserver() {
        viewModel.eventData.observe(this) {
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
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val postEventRequest = PostEventRequest(
            date = date,
            event_info = listOf(
                EventInfo(name, time, description, isSpecial, thumbnail, "")
            )
        )

        val token = AppEncryptedPref.get().getToken(this)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authorization token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.postEvent(token, postEventRequest)
    }


    private fun handleState(state: ApiState<EventData>) {
        when (state.state) {
            State.loading ->{
                Log.d("EventActivity", "Login started...")
                showLoading()
            }
            State.success -> {
                hideLoading()
                showAlert("Success", "Event created successfully!")
                Log.d("EventActivity", "Event data: ${state.data}")
            }
            State.error -> {
                hideLoading()
                showAlert("Error", state.message ?: "An unexpected error occurred")
                Log.e("EventActivity", "Error message: ${state.message}")
            }

            else -> {
                Log.d("LogInActivity", "Unhandled state: ${state.state}")
            }
        }
    }


}