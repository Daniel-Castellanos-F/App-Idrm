package com.madrid.appidrm.io.response

import com.madrid.appidrm.model.User

data class LoginResponse(val success: Boolean, val user: User, val jwt: String)