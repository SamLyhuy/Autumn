package kh.edu.rupp.ite.autumn.data.model

data class ApiState<T>(

    val state: State,
    val data: T?


) {

    companion object {
        fun <T> loading(): ApiState<T> {
            return ApiState(State.loading, null)
        }
    }


}

enum class State {
    none, loading, success, error
}
