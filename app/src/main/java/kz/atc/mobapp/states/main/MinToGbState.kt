package kz.atc.mobapp.states.main

sealed class MinToGbState {


    data class Exchanged(val status: String) : MinToGbState()

    data class EtQuantityChanged(val quantity: Int) : MinToGbState()

}