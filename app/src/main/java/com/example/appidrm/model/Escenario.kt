package com.example.appidrm.model

data class Escenario(val id: Int, val name: String, val description: String, val address: String){
    override fun toString(): String {
        return name
    }
}