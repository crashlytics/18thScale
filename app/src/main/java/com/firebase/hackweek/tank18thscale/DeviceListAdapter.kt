package com.firebase.hackweek.tank18thscale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.hackweek.tank18thscale.model.DeviceInfo

class DeviceListAdapter(private val deviceClickListener: DeviceClickListener) : RecyclerView.Adapter<DeviceListAdapter.DeviceInfoViewHolder>() {

    interface DeviceClickListener {
        fun onItemClick(deviceInfo: DeviceInfo)
    }

    private var devices = mutableListOf<DeviceInfo>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceInfoViewHolder {
        val deviceView = LayoutInflater.from(parent.context).inflate(R.layout.device_list_item, parent, false)
        return DeviceInfoViewHolder(deviceView)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceInfoViewHolder, position: Int) {
        holder.bind(devices[position], deviceClickListener)
    }

    fun setDevices(newDevices: List<DeviceInfo>) {
        val diffCallback = DevicesDiffCallback(devices, newDevices)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        devices.clear()
        devices.addAll(newDevices)

        diffResult.dispatchUpdatesTo(this)
    }

    class DeviceInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceNameView: TextView = itemView.findViewById(R.id.device_name)
        private val deviceAddressView: TextView = itemView.findViewById(R.id.device_address)

        fun bind(deviceInfo: DeviceInfo, deviceClickListener: DeviceClickListener) {
            deviceNameView.text = deviceInfo.name
            deviceAddressView.text = deviceInfo.address

            itemView.setOnClickListener { deviceClickListener.onItemClick(deviceInfo) }
        }
    }

    class DevicesDiffCallback(private val oldList: List<DeviceInfo>, private val newList: List<DeviceInfo>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].address == newList[newItemPosition].address
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}