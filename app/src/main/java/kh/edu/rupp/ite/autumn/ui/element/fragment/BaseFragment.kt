package kh.edu.rupp.ite.autumn.ui.element.fragment

import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.autumn.ui.element.activity.BaseActivity

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

}
