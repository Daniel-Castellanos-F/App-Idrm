package com.madrid.appidrm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.madrid.appidrm.R
import com.madrid.appidrm.io.ApiService
import com.madrid.appidrm.io.response.LoginResponse
import com.madrid.appidrm.util.PreferenceHelper
import com.madrid.appidrm.util.PreferenceHelper.set
import com.madrid.appidrm.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val apiService by lazy{
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvGoToLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnConfirmRegister.setOnClickListener{
            performRegister()
        }
    }

    private fun performRegister(){
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val password_confirmation = etRegisterPasswordConfirmation.text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || password_confirmation.isEmpty()){
            toast(getString(R.string.error_register_empty_fields))
            return
        }

        if (password != password_confirmation) {
            toast(getString(R.string.error_register_password_do_not_match))
            return
        }

        val call = apiService.postRegister(name, email, password, password_confirmation)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if( response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.jwt)
                        goToWelcomeActivity()
                    } else {
                        toast(getString(R.string.error_register_validation))
                    }

                }else{
                    toast(getString(R.string.error_register_validation))
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


}