package com.madrid.appidrm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.madrid.appidrm.R
import com.madrid.appidrm.io.ApiService
import com.madrid.appidrm.model.Appointment
import com.madrid.appidrm.util.PreferenceHelper
import com.madrid.appidrm.util.PreferenceHelper.get
import com.madrid.appidrm.util.toast
import kotlinx.android.synthetic.main.activity_my_appointment.*
import kotlinx.android.synthetic.main.activity_welcome.tvGoToMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
       ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointment)

        loadAppointments()

        rvMyAppointment.layoutManager = LinearLayoutManager(this)
        rvMyAppointment.adapter = appointmentAdapter

        tvGoToMenu.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)

        }
    }

    private fun loadAppointments(){
        val jwt = preferences["jwt",""]
        val call = apiService.getAppointments("Bearer $jwt")

        call.enqueue(object: Callback<ArrayList<Appointment>> {
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<ArrayList<Appointment>>, response: Response<ArrayList<Appointment>>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        appointmentAdapter.appointments = it
                        appointmentAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}