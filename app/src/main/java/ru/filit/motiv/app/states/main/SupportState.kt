package ru.filit.motiv.app.states.main

sealed class SupportState {
    object NoMessengers : SupportState()
    object BothMessengers: SupportState()
    object WhatsappMessengers: SupportState()
    object ViberMessengers: SupportState()
}