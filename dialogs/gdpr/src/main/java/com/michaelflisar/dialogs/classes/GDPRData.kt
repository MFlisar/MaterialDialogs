package com.michaelflisar.dialogs.classes

data class GDPRData(
    val location: GDPRLocation = GDPRLocation.UNDEFINED,
    val subNetworks: List<GDPRSubNetwork> = emptyList()
)