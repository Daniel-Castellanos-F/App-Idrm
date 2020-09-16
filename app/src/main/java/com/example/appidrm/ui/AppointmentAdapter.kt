package com.example.appidrm.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appidrm.R
import com.example.appidrm.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter
    : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    var appointments = ArrayList<Appointment>()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(appointment: Appointment) = with (itemView){
                tvAppointmentId.text = context.getString(R.string.item_appointment_id, appointment.id)
                tvEscenarioName.text = appointment.escenario.name
                tvScheduleDate.text = context.getString(R.string.item_appointment_date, appointment.scheduleDate)
                tvScheduleTime.text = context.getString(R.string.item_appointment_time, appointment.scheduleTime)

                tvAddress.text = appointment.escenario.address
                tvMotivo.text = appointment.motivo
                tvstatus.text = appointment.status
                tvCreateAt.text = context.getString(R.string.item_appointment_created_at, appointment.createdAt)
        }
    }

    // crear la vista apartir del Xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
    }

    //devolver las cantidad de elementos
    override fun getItemCount() = appointments.size


    // enlazar la data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }
}