package com.example.appidrm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.appidrm.*
import com.example.appidrm.io.ApiService
import com.example.appidrm.util.PreferenceHelper
import com.example.appidrm.util.PreferenceHelper.set
import com.example.appidrm.util.PreferenceHelper.get
import com.example.appidrm.util.toast
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiServise: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        MenuClose.setOnClickListener{
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_CreateAppointmet.setOnClickListener{
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_MyAppointment.setOnClickListener{
            val intent = Intent(this, MyAppointmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_TrainingSchools.setOnClickListener{
            val intent = Intent(this, TrainingSchoolsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_logOut.setOnClickListener{
            performLogout()
        }
    }

    private fun performLogout(){
        val jwt = preferences["jwt",""]
        val call = apiServise.postLogout("Bearer $jwt")
        call.enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()

                val intent = Intent(this@MenuActivity,MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        })
    }
    private fun clearSessionPreference(){
        preferences["jwt"] = ""
    }

    override fun onBackPressed() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}