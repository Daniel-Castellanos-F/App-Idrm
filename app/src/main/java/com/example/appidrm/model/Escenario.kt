package com.example.appidrm.model

data class Escenario(
        val id: Int,
        val name: String,
        val description: String,
        val address: String,
        val longitud: Double,
        val latitud:Double
    ){
    override fun toString(): String {
        return name
    }
}