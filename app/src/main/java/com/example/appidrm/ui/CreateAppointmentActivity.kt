package com.example.appidrm.ui

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.appidrm.EscenariosMaps
import com.example.appidrm.R
import com.example.appidrm.io.ApiService
import com.example.appidrm.io.response.SimpleResponse
import com.example.appidrm.model.Escenario
import com.example.appidrm.model.Schedule
import com.example.appidrm.util.PreferenceHelper
import com.example.appidrm.util.PreferenceHelper.get
import com.example.appidrm.util.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.activity_welcome.tvGoToMenu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }
    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }
    private var selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? = null

    var longitud: Double = -74.26264429999999
    var latitud:Double = 4.7343337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        tvGoToMenu.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewEscenario.setOnClickListener{
            val intent = Intent(this, EscenariosMaps::class.java)
            intent.putExtra("longitud",longitud)
            intent.putExtra("latitud",latitud)
            startActivity(intent)
        }

        btnNetx1.setOnClickListener{
            if(etMotivo.text.toString().length < 6){
                etMotivo.error = getString(R.string.validate_appointment_motivo)
            }else  if( etScheduleDate.text.toString().isEmpty() ){
                etScheduleDate.error = getString(R.string.validate_appointment_date)
            }else if(selectedTimeRadioButton == null){
                Snackbar.make(createAppointmentLayout,R.string.validate_appointment_time, Snackbar.LENGTH_SHORT).show()
            }else{
                showAppointmentDataToConfirm()
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btn_tvConfirmAppointment.setOnClickListener{
            performStoreAppointment()
        }
        loadEscenarios()
        listenEscenarioAndDateChanges()
    }

    private fun performStoreAppointment(){
        btn_tvConfirmAppointment.isClickable = false
        val intent = Intent(this,WelcomeActivity::class.java)
        val jwt = preferences["jwt", ""]
        val authHeader = "Bearer $jwt"
        val motivo = tvConfirmMotivo.text.toString()
        val escenario = spinnerEscenarios.selectedItem as Escenario
        val scheduleDate = tvConfirmScheduleDate.text.toString()
        val scheduleTime = tvConfirmScheduleTime.text.toString()


        val call = apiService.storeAppointments(authHeader, escenario.id, scheduleDate, scheduleTime, motivo)
        call.enqueue(object: Callback<SimpleResponse>{
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if(response.isSuccessful){
                    toast(getString(R.string.create_appointment_success))
                    startActivity(intent)
                    finish()
                }else{
                    toast(getString(R.string.create_appointment_error))
                    btn_tvConfirmAppointment.isClickable = true
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                toast(t.localizedMessage)
                btn_tvConfirmAppointment.isClickable = true
            }

        })
    }

    private fun loadEscenarios(){
        val call= apiService.getEscenarios()
        call.enqueue(object: Callback<ArrayList<Escenario>>{

            override fun onFailure(call: Call<ArrayList<Escenario>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_escenarios), Toast.LENGTH_SHORT).show()
                finish()
            }
            override fun onResponse(call: Call<ArrayList<Escenario>>, response: Response<ArrayList<Escenario>>) {
                if (response.isSuccessful){
                    val escenarios = response.body()
                    spinnerEscenarios.adapter = ArrayAdapter<Escenario>(this@CreateAppointmentActivity,android.R.layout.simple_list_item_1, escenarios!!)
                }
            }
        })
    }

    private fun listenEscenarioAndDateChanges()
    {
        //lister escenarios
        spinnerEscenarios.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>){
            }
            override fun onItemSelected(adapter: AdapterView<*>?, view:View?, position: Int, id: Long){
                val escenario = adapter?.getItemAtPosition(position) as Escenario
                longitud = escenario.longitud
                latitud = escenario.latitud
                loadHours(escenario.id, etScheduleDate.text.toString())
            }
        }
        //scheduled date
        etScheduleDate.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val escenario = spinnerEscenarios.selectedItem as Escenario
                loadHours(escenario.id, etScheduleDate.text.toString())
            }

        })
    }

    private fun loadHours(escenarioId: Int, date: String){

        if (date.isEmpty()){
            return
        }
        val call = apiService.getHours(escenarioId, date)
        call.enqueue(object: Callback<Schedule>{
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.Error_loadin_hours), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful){
                    val schedule = response.body()
                    //Toast.makeText(this@CreateAppointmentActivity,"morning: ${schedule?.morning?.size} , afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                    // val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
                    schedule?.let {
                        tvSelectEscenarioAndDate.visibility = View.GONE
                        val intervals = it.morning + it.afternoon
                        val hours = ArrayList<String>()
                        intervals.forEach{ interval ->
                            hours.add(interval.start)
                        }
                        displayIntervalRadios(hours)
                    }

                }
            }

        })
        //Toast.makeText(this,"escenario: $escenarioId, date: $date",Toast.LENGTH_SHORT).show()
    }

    private fun showAppointmentDataToConfirm(){
        val escenario = spinnerEscenarios.selectedItem as Escenario
        tvConfirmEscenario.text = spinnerEscenarios.selectedItem.toString()
        tvConfirmMotivo.text = etMotivo.text.toString()
        tvConfirmScheduleDate.text = etScheduleDate.text.toString()
        tvConfirmScheduleTime.text = selectedTimeRadioButton?.text.toString()
        tvConfirmAddres.text = escenario.address
    }

    fun onClickScheduleDate(v: View?){

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y , m ,d ->
            selectedCalendar.set(y, m, d)

            etScheduleDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    (m+1).twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduleDate.error = null

        }

        val datePickerDialog =  DatePickerDialog(this,listener, year,month, dayOfMonth )

        //limits
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,1)
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH,19)
        datePicker.maxDate = calendar.timeInMillis

        //show dialog
        datePickerDialog.show()
    }

    private fun displayIntervalRadios(hours: ArrayList<String>){
        selectedTimeRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        if (hours.isEmpty()){
            tvNotAvailableHours.visibility = View.VISIBLE
            return
        }

        tvNotAvailableHours.visibility = View.GONE
        // val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM","4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener{ view ->
                selectedTimeRadioButton?.isChecked = false
                selectedTimeRadioButton = view as RadioButton?
                selectedTimeRadioButton?.isChecked = true
            }
            if (goToLeft) {
                radioGroupLeft.addView(radioButton)
            } else
                radioGroupRight.addView(radioButton)
            goToLeft =!goToLeft
        }

    }

    private fun Int.twoDigits() = if(this>=10) this.toString() else "0$this"

    override fun onBackPressed() {

        if (cvStep2.visibility == View.VISIBLE) {
            cvStep2.visibility = View.GONE
            cvStep1.visibility = View.VISIBLE
        }else if (cvStep1.visibility == View.VISIBLE){
            val builder  = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.dialog_create_reser_exit_title))
            builder.setMessage(getString(R.string.dialog_create_reserv_message))
            builder.setPositiveButton(getString(R.string.dialog_create_reser_exit_positive_btn)){ _, _ ->
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton(getString(R.string.dialog_create_reser_exit_negative_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}