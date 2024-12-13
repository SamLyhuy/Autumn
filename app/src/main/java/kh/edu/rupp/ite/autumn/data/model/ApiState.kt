package kh.edu.rupp.ite.autumn.data.model


data class ApiState<T>(

    val state: State,
    val message: String?,
    val data: T?


) {

    companion object {
        fun <T> loading(): ApiState<T> {
            return ApiState(State.loading,null,null)
        }

        fun <T> success(comments: T): ApiState<T> {
            return  ApiState(State.success,null, comments)
        }

        fun <T> error(message: String?): ApiState<T> {
            return  ApiState(State.error, message, null)
        }

    }


}

enum class State {
    none, loading, success, error
}
