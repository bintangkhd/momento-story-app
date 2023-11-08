package com.example.storyappsubmission.data.paging

sealed class ResultCondition<out R> private constructor() {
    data class SuccessState<out T>(val data: T) : ResultCondition<T>()
    data class ErrorState(val data: String) : ResultCondition<Nothing>()
    object LoadingState : ResultCondition<Nothing>()
}