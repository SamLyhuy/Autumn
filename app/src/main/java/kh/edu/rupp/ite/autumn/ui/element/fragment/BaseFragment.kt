// ─── BaseFragment.kt ──────────────────────────────────────────────────────────
package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper

import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.databinding.DialogServerResponseBinding
import kh.edu.rupp.ite.autumn.ui.element.activity.BaseActivity
import kh.edu.rupp.ite.autumn.ui.element.adapter.Event
import kh.edu.rupp.ite.autumn.data.model.UiMessage

open class BaseFragment : Fragment() {

    fun showLoading() {
        val hostActivity = activity as BaseActivity?
        hostActivity?.showLoading()
    }

    fun hideLoading() {
        val hostActivity = activity as BaseActivity?
        hostActivity?.hideLoading()
    }

    fun showAlert(title: String, message: String) {
        val hostActivity = activity as BaseActivity?
        hostActivity?.showAlert(title, message)
    }

    protected fun observeServerResponse(messageLiveData: LiveData<Event<UiMessage>>) {
        messageLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { uiMsg ->
                showCustomDialog(uiMsg.text, uiMsg.isSuccess)
            }
        }
    }


    private fun showCustomDialog(text: String, isSuccess: Boolean) {
        // 1) Create Dialog without title bar
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 2) Inflate our dialog_server_response.xml via ViewBinding
        val binding = DialogServerResponseBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        // 3) Populate icon + text
        binding.tvServerMessage.text = text
        if (isSuccess) {
            binding.ivStatusIcon.setImageResource(R.drawable.ic_success)
        } else {
            binding.ivStatusIcon.setImageResource(R.drawable.ic_error)
        }

        // 4) Hide the manual dismiss button (we auto-close instead)
        binding.btnDismiss.visibility = android.view.View.GONE

        // 5) Show the dialog, then force wrap_content so it doesn't stretch
        dialog.show()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // 6) Auto‐dismiss after 1.5 seconds (1500ms)
        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 1500)
    }
}
