import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.databinding.DetailEventBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.BaseFragment

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

        // Populate UI with data
        Picasso.get().load(eventInfo?.thumbnail).into(binding.eventImage)
        binding.eventName.text = eventInfo?.name ?: "No event info available"
        binding.eventDescription.text = eventInfo?.description ?: "No event info available"
        binding.eventTime.text = eventInfo?.time ?: "No date provided"

        // Back button logic
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}

