package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.autumn.databinding.ActivityEventBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding


class EventFragment: Fragment() {

   private lateinit var binding: ActivityEventBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = ActivityEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}