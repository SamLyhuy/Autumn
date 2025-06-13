import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.databinding.DetailEventBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.BaseFragment
import kh.edu.rupp.ite.autumn.ui.element.fragment.TableBookingFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventDetailFragment : BaseFragment() {

    private lateinit var binding: DetailEventBinding
    private var eventInfo: EventInfo? = null
    private var eventDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailEventBinding.inflate(inflater, container, false)

        // Retrieve arguments safely
        eventInfo = arguments?.getParcelable("event_info")
        eventDate = arguments?.getString("event_date")

        // Debugging logs
        Log.d("EventDetailFragment", "EventInfo: $eventInfo")
        Log.d("EventDetailFragment", "EventDate: $eventDate")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setUpListener()
    }

    /** Populate all UI fields from the received data */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpObserver() {

        val inputFmt = DateTimeFormatter.ISO_LOCAL_DATE
        val outputFmt = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
        val date = LocalDate.parse(eventDate, inputFmt)

        Picasso.get()
            .load(eventInfo?.thumbnail)
            .into(binding.eventImage)

        binding.eventName.text = eventInfo?.name
            ?: "No event info available"

        binding.eventDate.text = date.format(outputFmt) ?: "No event info available"

        binding.eventDescription.text = eventInfo?.description
            ?: "No event info available"

        binding.eventTime.text = eventInfo?.time
            ?: "No date provided"
    }

    /** Wire any click-listeners or other view callbacks */
    private fun setUpListener() {
        binding.btnBackEvent.setOnClickListener {
            navigateBack()
        }
        binding.btnBooking.setOnClickListener{
            navigateToTableBookingFragment()
        }
    }

    private fun navigateToTableBookingFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.eventDetail, TableBookingFragment())
            .addToBackStack(null)
            .commit()
    }

    /** Handle navigating back to the previous fragment */
    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }
}
