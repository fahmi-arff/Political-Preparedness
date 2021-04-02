package com.example.android.politicalpreparedness.utils

import com.example.android.politicalpreparedness.network.models.Address
import java.util.*

fun Address.isValid(): Boolean {
    return this.line1.isNotEmpty() && this.city.isNotEmpty() && this.state.isNotEmpty() &&
            this.zip.isNotEmpty() && this.zip.length == 5
}

fun getToday(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}