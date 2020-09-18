package com.madrid.appidrm.model
/*
        "id": 3,
        "name": "Paola Silva",
        "email": "usuario@example.net",
        "cedula": "1074186029",
        "address": null,
        "role": "usuario"
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val cedula: String,
    val address: String,
    val role: String

)