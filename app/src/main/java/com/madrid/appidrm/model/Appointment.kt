package com.madrid.appidrm.model

import com.google.gson.annotations.SerializedName

/*
{
    "id": 1,
    "schedule_date": "2020-09-12",
    "motivo": null,
    "created_at": "2020-09-07 19:58:57",
    "status": "Cancelada",
    "schedule_time_12": "10:00 AM",
    "escenario": {
        "id": 3,
        "name": "Piscina los Azulejos",
        "description": "Piscina para el aprendizaje de la practica de la natación para niños entre los 5 y 10 años",
        "address": "Avenida siempre viva 12 # 6-6"
    }
},
 */
data class Appointment (
    val id: Int,
    val motivo: String,
    val status: String,
    @SerializedName("schedule_date") val scheduleDate: String,
    @SerializedName("schedule_time_12") val scheduleTime: String,
    @SerializedName("created_at") val createdAt: String,
    val escenario: Escenario
)