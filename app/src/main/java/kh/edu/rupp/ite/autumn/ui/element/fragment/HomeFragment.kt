package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Comment
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.data.model.Test
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel


class HomeFragment: BaseFragment() {

    private val viewModel by viewModels<HomeViewModel>()

   private lateinit var binding: ActivityHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = ActivityHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homeData.observe(viewLifecycleOwner) { state ->

        }

        viewModel.loadingHomeData()
    }

    private fun handleState(state: ApiState<Comment>) {
        viewModel.homeData.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> { showLoading() }
                State.success -> {
                    hideLoading()
                    val data = state.data
                    Log.d("HomeData", "Data Loaded: $data")
                    if (data != null) {
                        showHomeData(data)  // Show the data in the adapter
                    }
                }
                State.error -> {
                    hideLoading()
                    Log.e("HomeData", "Error: ${state.message}")
                    showAlert("Error", state.message ?: "Unexpected Error")
                }
                else -> {}
            }
        }

    }

    private fun showHomeData(comment: List<Comment>){
        val itemFoodLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemFoodAdapter = EventAdapter()
        binding.popularFoodRecycler.apply {
            adapter = itemFoodAdapter
            layoutManager = itemFoodLayoutManager
        }


    }
}