package de.paulweber.spenderino.model.repositories.user

sealed interface UserState {
    object Error : UserState
    object Loading : UserState
    data class Anonymous(val user: User) : UserState
    data class Registered(val user: User, val profileState: ProfileState) : UserState
}
