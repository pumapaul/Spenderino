package de.paulweber.spenderino.model.repositories.user

sealed interface ProfileState {
    object Error : ProfileState
    object Loading : ProfileState
    object None : ProfileState
    data class Present(val profile: Profile) :
        ProfileState
}
