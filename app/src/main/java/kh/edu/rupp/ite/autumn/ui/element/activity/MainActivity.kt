package kh.edu.rupp.ite.autumn.ui.element.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.databinding.ActivityMainBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.*

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment    = HomeFragment()
    private val bookingFragment = BookingFragment()
    private val eventFragment   = EventFragment()
    private val accountFragment = AccountFragment()
    private val chatBotFragment = ChatBotFragment()  // single chat instance

    private lateinit var activeFragment: Fragment
    private var isChatVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        setupBottomNavBar()
        showChatIntro()


    }

    private fun setupFragments() {
        // Start with Home showing, everything else hidden (including chat)
        activeFragment = homeFragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.lytFragment, homeFragment)
            add(R.id.lytFragment, bookingFragment).hide(bookingFragment)
            add(R.id.lytFragment, eventFragment).hide(eventFragment)
            add(R.id.lytFragment, accountFragment).hide(accountFragment)
            add(R.id.lytFragment, chatBotFragment).hide(chatBotFragment)
        }.commit()
    }

    private fun setupBottomNavBar() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            // if chat is up, close it via your helper
            if (isChatVisible) {
                closeChat()
            }
            // now switch to the tapped tab
            handleOnNavigationItemSelected(item)
        }

        binding.fabChatBot.setOnClickListener {
            openChatBot()
        }
    }

    /** Hides the chat and brings back the last nav fragment + bottom bar */
    fun closeChat() {
        if (!isChatVisible) return

        supportFragmentManager.beginTransaction()
            .hide(chatBotFragment)
            .show(activeFragment)
            .commit()

        //binding.bottomNavigationView.visibility = View.VISIBLE
        isChatVisible = false
        binding.fabChatBot.visibility = View.VISIBLE
    }

    private fun handleOnNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home    -> showFragment(homeFragment)
            R.id.booking -> showFragment(bookingFragment)
            R.id.event   -> showFragment(eventFragment)
            R.id.account -> showFragment(accountFragment)
            else         -> return false
        }
        return true
    }

    private fun openChatBot() {
        // Hide the current nav fragment, show the chat, hide the bottom bar
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(chatBotFragment)
            .commit()

        isChatVisible = true
        binding.fabChatBot.visibility = View.GONE
        binding.chatIntroCard.visibility = View.GONE
    }


    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment)
            show(fragment)
            commit()
        }
        activeFragment = fragment
        binding.fabChatBot.visibility = View.VISIBLE
    }

    /** Briefly shows the “I’m ChatBot…” bubble, then hides it. */
    private fun showChatIntro() {
        // 1) make it visible
        binding.chatIntroCard.visibility = View.VISIBLE

        // 2) after 3 seconds, auto-hide
        binding.chatIntroCard.postDelayed({
            binding.chatIntroCard.visibility = View.GONE
        }, 5000L)
    }
}
