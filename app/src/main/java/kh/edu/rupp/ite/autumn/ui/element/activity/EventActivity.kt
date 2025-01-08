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

        }
    }

    private fun setObserver() {
        viewModel.eventData.observe(this) {
            handleState(it)
        }
    }

    private fun handleState(state: ApiState<EventData>) {
        when (state.state) {
            State.loading ->{
                Log.d("EventActivity", "Creating...")
                showLoading()
            }
            State.success -> {
                hideLoading()
                Log.d("EventActivity", "Event created successfully!")
                Log.d("EventActivity", "Event data: ${state.data}")
            }
            State.error -> {
                hideLoading()
                showAlert("EventActivity", state.message ?: "An unexpected error occurred")
                Log.e("EventActivity", "Error message: ${state.message}")
            }

            else -> {
                Log.d("EventActivity", "Unhandled state: ${state.state}")
            }
        }
    }


}