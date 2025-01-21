package kh.edu.rupp.ite.autumn.ui.element.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    fun showLoading() {
        if(progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }

        progressDialog!!.show()
    }

    fun hideLoading() {
        progressDialog?.hide()
    }

    fun showAlert(title: String, message: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setPositiveButton("OK", null)
        dialog.show()
    }



}
