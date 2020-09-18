package com.madrid.appidrm.model

import com.google.gson.annotations.SerializedName


data class Appointment (
    val id: Int,
    val motivo: String,
    val status: String,
    @SerializedName("schedule_date") val scheduleDate: String,
    @SerializedName("schedule_time_12") val scheduleTime: String,
    @SerializedName("created_at") val createdAt: String,
    val escenario: Escenario
)