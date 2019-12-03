package com.firebase.hackweek.tank18thscale

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceListAdapter : RecyclerView.Adapter<DeviceListAdapter.DeviceInfoViewHolder>() {

    class DeviceInfoViewHolder(itemView: View, private val adapter: DeviceListAdapter) : RecyclerView.ViewHolder(itemView) {
        constructor() {

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceListAdapter.DeviceInfoViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: DeviceListAdapter.DeviceInfoViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}