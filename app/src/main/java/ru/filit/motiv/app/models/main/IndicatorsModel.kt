package ru.filit.motiv.app.models.main

data class IndicatorsModel(
    var dataIndicators: MutableList<IndicatorHolder> = mutableListOf(),
    var voiceIndicators: MutableList<IndicatorHolder> = mutableListOf(),
    var smsIndicators: MutableList<IndicatorHolder> = mutableListOf()
)