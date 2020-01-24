package kz.atc.mobapp.states.main

sealed class BlockUnblockDialogState {

    data class RequestProcessed(val message:String, val incorrectCounter:Int?=null, val isProcessed: Boolean = true) : BlockUnblockDialogState()

    object Loading : BlockUnblockDialogState()

}