package kz.atc.mobapp.models.main

data class IndicatorsModel(
    var dataIndicators: MutableList<IndicatorHolder>,
    var voiceIndicators: MutableList<IndicatorHolder>,
    var smsIndicators: MutableList<IndicatorHolder>
)