package com.example.appidrm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.appidrm.R
import com.example.appidrm.io.ApiService
import com.example.appidrm.util.PreferenceHelper
import com.example.appidrm.util.PreferenceHelper.get
import com.example.appidrm.util.toast
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_welcome.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {

    private val apiServise: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val storeToken = intent.getBooleanExtra("store_token", false)
        if(storeToken)
            storeToken()

        tvGoToMenu.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun storeToken(){
        val jwt = preferences["jwt",""]
        val authHeader = "Bearer $jwt"

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val deviceToken = instanceIdResult.token

            val call = apiServise.postToken(authHeader, deviceToken)
            call.enqueue(object: Callback<Void>{
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Log.d(TAG, "Token registrado correctamente")
                    }else{
                        Log.d(TAG, "Hubo un problema al registrar el token")
                    }
                }
            })
        }
    }

    companion object {
        private const val TAG = "WelcomeActivity"
    }
}