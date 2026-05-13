package com.example.clients.ui.home.states

sealed class HomeEvent {

    data class ShowMessage( val resId: Int) : HomeEvent()
}