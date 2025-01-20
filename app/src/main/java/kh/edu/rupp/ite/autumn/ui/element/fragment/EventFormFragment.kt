package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
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
import java.util.*

class EventFormFragment : BaseFragment() {

    private lateinit var binding: ActivityPostEventBinding
    private val viewModel by viewModels<EventViewModel>()

    private var selectedThumbnailUri: Uri? = null
    private val IMAGE_PICK_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityPostEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupUi()
        setUpListener()
        setObserver()
    }


    private fun onClickDate() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format year, month, and day with zero-padding
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.tvDate.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun setUpListener() {
        binding.tvDate.setOnClickListener {
            onClickDate()
        }

        binding.btnThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnSubmitEvent.setOnClickListener {
            onEventButtonClick()
        }
    }

    private fun navigateBack() {
        // Use the appropriate navigation method for your project
        parentFragmentManager.popBackStack() // Navigates back to the previous fragment
    }

    private fun onEventButtonClick() {
        val date = binding.tvDate.text.toString().trim()
        val name = binding.etNameEvent.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val isSpecial = binding.switchIsSpecial.isChecked

        Log.d("EventFormFragment", "Got: $date, $name, $time, $description, $isSpecial")


//        if (date.isEmpty() || name.isEmpty() || time.isEmpty() || description.isEmpty() || selectedThumbnailUri == null) {
//            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            return
//        }

        val postEventRequest = PostEventRequest(
            date = date,
            event_info = listOf(
                EventInfo(name, time, description, isSpecial, selectedThumbnailUri.toString(), null)
            )
        )

        val token = AppEncryptedPref.get().getToken(requireContext())

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authorization token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.postEvent(token, postEventRequest)
    }

    private fun setObserver() {
        viewModel.eventData.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: ApiState<EventData>) {
        when (state.state) {
            State.loading -> {
                // Show loading UI
                Log.d("EventFormFragment", "Loading...")
            }
            State.success -> {
                Toast.makeText(requireContext(), "Event submitted successfully!", Toast.LENGTH_SHORT).show()
                Log.d("EventFormFragment", "Success: ${state.message}")
                navigateBack()
            }
            State.error -> {
                Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                Log.e("EventFormFragment", "Error: ${state.message}")
            }
            else -> {
                Log.w("EventFormFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedThumbnailUri = data?.data
            binding.btnThumbnail.text = "Thumbnail Selected"
        }
    }
}
