package kh.edu.rupp.ite.autumn.data.model

data class LogInState(
    val state: StateLogIn,
    val message: String? = null,
    val token: String? = null,
) {
    companion object {
        fun loading(): LogInState {
            return LogInState(state = StateLogIn.loading)
        }

        fun success(response: LogInResponse): LogInState {
            return LogInState(state = StateLogIn.success, message = response.message, token = response.token)

        }

        fun error(message: String): LogInState {
            return LogInState(state = StateLogIn.error, message = message)
        }
    }
}

enum class StateLogIn {
    none, loading, success, error
}
