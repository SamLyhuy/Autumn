package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.autumn.databinding.ActivityBookingBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding


class BookingFragment: Fragment() {

   private lateinit var binding: ActivityBookingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = ActivityBookingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}