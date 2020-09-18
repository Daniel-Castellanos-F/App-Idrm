package com.madrid.appidrm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.madrid.appidrm.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.madrid.appidrm.util.PreferenceHelper.get
import com.madrid.appidrm.util.PreferenceHelper.set
import com.madrid.appidrm.R
import com.madrid.appidrm.io.ApiService
import com.madrid.appidrm.io.response.LoginResponse
import com.madrid.appidrm.util.toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val apiServise: ApiService by lazy{
        ApiService.create()
    }
    private val snackBar by lazy {
        Snackbar.make(mainLayout,
            R.string.press_back_again,Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //share preferences

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["jwt", ""].contains("."))
            goToWelcomeActivity()

        login.setOnClickListener{
            // validar login
            performLogin()
        }
        tvGoToRegister.setOnClickListener{
            Toast.makeText(this, getString(R.string.please_fill_in_your_details), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun performLogin(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isEmpty() || password.trim().isEmpty()){
            toast(getString(R.string.error_empty_credentials))
            return
        }

       val call = apiServise.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.jwt)
                        goToWelcomeActivity()
                    } else {
                        toast(getString(R.string.error_invalid_credentials))
                    }
                }else {
                    toast(getString(R.string.error_login_response))
                }
            }
        })
    }

    private fun createSessionPreference(jwt: String){
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = jwt

    }

    private fun goToWelcomeActivity(){
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()

    }
}