package kh.edu.rupp.ite.autumn.data.model

data class ApiState<T>(
    val state: State,
    val message: String?,
    val token: String? = null,
    val data: T?
){
    companion object {
        fun <T> loading(): ApiState<T> {
            return ApiState(State.loading, null, null, null)
        }

    fun <T> success(data: T, token: String? = null, message: String? = null): ApiState<T> {
        return ApiState(State.success, message, token, data)
    }

        fun <T> error(message: String?): ApiState<T> {
            return ApiState(State.error, message, null, null)
        }
    }

}

enum class State {
    none, loading, success, error
}
