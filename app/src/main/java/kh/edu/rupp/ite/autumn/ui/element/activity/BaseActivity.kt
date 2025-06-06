// app/src/main/java/kh/edu/rupp/ite/autumn/ui/element/activity/BaseActivity.kt
package kh.edu.rupp.ite.autumn.ui.element.activity

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.FrameLayout
import kh.edu.rupp.ite.autumn.R

/**
 * A simple BaseActivity that provides:
 *   - showLoading() / hideLoading() to toggle the full-screen overlay
 *   - showAlert(...) to pop up an AlertDialog
 *
 * It does NOT assume ViewBinding or DataBinding. Instead, it just calls findViewById(...)
 * to locate the overlay in the current Activity’s inflated layout.
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Nothing to do here; subclasses will call setContentView(...) themselves.
    }

    /**
     * Show the full-screen “spinner overlay.”
     * It looks for R.id.loadingOverlay and R.id.imgLoadingSpinner inside whatever layout
     * was set via setContentView(...) in the subclass.
     */
    fun showLoading() {
        // Find the overlay container
        val overlay = findViewById<FrameLayout>(R.id.loadingOverlay)
        val spinner = findViewById<ImageView>(R.id.imgLoadingSpinner)
        overlay?.visibility = View.VISIBLE
        // Start the AnimationDrawable (frame-animation) if it’s set on the ImageView
        (spinner?.drawable as? AnimationDrawable)?.start()
    }

    /**
     * Hide the overlay and stop its AnimationDrawable.
     */
    fun hideLoading() {
        val overlay = findViewById<FrameLayout>(R.id.loadingOverlay)
        val spinner = findViewById<ImageView>(R.id.imgLoadingSpinner)
        (spinner?.drawable as? AnimationDrawable)?.stop()
        overlay?.visibility = View.GONE
    }

    /**
     * Show a simple Ok-only AlertDialog.
     */
    fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}

/**
 * Simple version
 */

//open class BaseActivity: AppCompatActivity() {
//
//    private var progressDialog: ProgressDialog? = null
//
//    fun showLoading() {
//        if(progressDialog == null) {
//            progressDialog = ProgressDialog(this)
//        }
//
//        progressDialog!!.show()
//    }
//
//    fun hideLoading() {
//        progressDialog?.hide()
//    }
//
//    fun showAlert(title: String, message: String) {
//        val dialog = AlertDialog.Builder(this)
//        dialog.setTitle(title)
//        dialog.setMessage(message)
//        dialog.setPositiveButton("OK", null)
//        dialog.show()
//    }
//
//
//
//}

