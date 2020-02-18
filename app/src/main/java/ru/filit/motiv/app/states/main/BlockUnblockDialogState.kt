package ru.filit.motiv.app.states.main

sealed class BlockUnblockDialogState {

    data class RequestProcessed(val message:String, val incorrectCounter:Int?=null, val isProcessed: Boolean = true) : BlockUnblockDialogState()

    data class InternetState(val active: Boolean): BlockUnblockDialogState()

    object Loading : BlockUnblockDialogState()

}