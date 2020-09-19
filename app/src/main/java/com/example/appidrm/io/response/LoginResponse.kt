package com.example.appidrm.io.response

import com.example.appidrm.model.User

data class LoginResponse(val success: Boolean, val user: User, val jwt: String)