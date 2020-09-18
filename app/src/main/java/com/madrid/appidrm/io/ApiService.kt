package com.example.appidrm.io

import com.example.appidrm.io.response.LoginResponse
import com.example.appidrm.io.response.SimpleResponse
import com.example.appidrm.model.Appointment
import com.example.appidrm.model.Escenario
import com.example.appidrm.model.Schedule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @GET("Escenarios")
    abstract fun getEscenarios():Call<ArrayList<Escenario>>

    @GET( "schedule/hours")
    fun getHours(@Query("escenario_id") escenarioId: Int, @Query("date") date: String):
            Call<Schedule>

    @POST( "login")
    fun postLogin(@Query("email") email: String, @Query("password") password: String):
            Call<LoginResponse>

    @POST( "logout")
    fun postLogout(@Header("Authorization") authHeader: String): Call<Void>

    @GET( "appointments")
    fun getAppointments(@Header("Authorization") authHeader: String):
            Call<ArrayList<Appointment>>

    @POST( "appointments")
    @Headers("Accept: application/json")
    fun storeAppointments(
        @Header("Authorization") authHeader: String,
        @Query("escenario_id") escenarioId: Int,
        @Query("schedule_date") scheduleDate: String,
        @Query("schedule_time") scheduleTime: String,
        @Query("motivo") motivo: String
    ): Call<SimpleResponse>

    @POST( "register")
    @Headers("Accept: application/json")
    fun postRegister(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("password_confirmation") password_confirmation: String
    ): Call<LoginResponse>

    companion object Factory{
        private const val BASE_URL = "http://167.172.29.228/api/"
        fun create(): ApiService{
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

    }


}