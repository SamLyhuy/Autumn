import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.databinding.DetailEventBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.BaseFragment

class EventDetailFragment : BaseFragment() {

    private lateinit var binding: DetailEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailEventBinding.inflate(inflater, container, false)

        // Retrieve arguments
        arguments?.getParcelable<EventData>("event_data")?.let { eventData ->
            binding.textEventName.text = eventData.name
            // Use Picasso or another library to load the image if needed
            // Picasso.get().load(eventData.thumbail).into(binding.imageViewThumbnail)
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the back button click listener
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack() // Pops the current fragment and returns to the previous one
        }
    }
}
