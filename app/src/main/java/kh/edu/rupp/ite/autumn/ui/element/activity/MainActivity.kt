package kh.edu.rupp.ite.autumn.ui.element.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.databinding.ActivityMainBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.AccountFragment
import kh.edu.rupp.ite.autumn.ui.element.fragment.BookingFragment
import kh.edu.rupp.ite.autumn.ui.element.fragment.EventFragment
import kh.edu.rupp.ite.autumn.ui.element.fragment.HomeFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val bookingFragment = BookingFragment()
    private val eventFragment = EventFragment()
    private val accountFragment = AccountFragment()
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize fragments
        setupFragments()

        // Handle bottom navigation selection
        setupBottomNavBar()
    }

    private fun setupFragments() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        activeFragment = homeFragment

        // Add fragments to the container, showing only the home fragment
        fragmentTransaction.add(R.id.lytFragment, homeFragment)
        fragmentTransaction.add(R.id.lytFragment, bookingFragment).hide(bookingFragment)
        fragmentTransaction.add(R.id.lytFragment, eventFragment).hide(eventFragment)
        fragmentTransaction.add(R.id.lytFragment, accountFragment).hide(accountFragment)
        fragmentTransaction.commit()
    }

    private fun setupBottomNavBar() {
        // Listen for bottom navigation selections
        binding.bottomNavigationView.setOnItemSelectedListener {
            handleOnNavigationItemSelected(it)
        }
    }

    private fun handleOnNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> showFragment(homeFragment)
            R.id.booking -> showFragment(bookingFragment)
            R.id.event -> showFragment(eventFragment)
            R.id.account -> showFragment(accountFragment)
            else -> return false
        }
        return true
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment)
        fragmentTransaction.show(fragment)
        activeFragment = fragment
        fragmentTransaction.commit()
    }


}
