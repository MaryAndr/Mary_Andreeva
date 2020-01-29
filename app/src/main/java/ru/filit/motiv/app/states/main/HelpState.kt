package ru.filit.motiv.app.states.main

sealed class HelpState {
    object NoMessengers : HelpState()
    object BothMessengers: HelpState()
    object WhatsappMessengers: HelpState()
    object ViberMessengers: HelpState()
}