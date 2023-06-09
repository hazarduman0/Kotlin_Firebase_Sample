package com.example.visitravel.models

import java.util.Date

data class Location(
    var locationId: String?,
    var locationName: String,
    var locationDescription: String,
    var date: Date
)
