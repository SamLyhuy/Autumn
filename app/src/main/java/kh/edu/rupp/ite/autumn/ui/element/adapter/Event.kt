package kh.edu.rupp.ite.autumn.ui.element.adapter

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /** Return the content if not handled yet; else return null. */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /** Allow peeking at the raw content even if it’s already been handled. */
    fun peekContent(): T = content
}
