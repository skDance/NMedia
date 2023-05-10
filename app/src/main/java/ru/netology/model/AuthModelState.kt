package ru.netology.model

data class AuthModelState(
    val authLoading: Boolean = false,
    val authSuccessful: Boolean = false,
    val authError: Boolean = false,
)