package com.firebase.hackweek.tank18thscale

import androidx.lifecycle.ViewModel
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData





class DevicesViewModel(private val devices : List<DeviceInfo>) : ViewModel() {

}

class UsersViewModel : ViewModel() {
    private val userLiveData = MutableLiveData<List<User>>()

    val userList: LiveData<List<User>>
        get() = userLiveData

    init {
        setUserListRefreshCallback { newUserList -> userLiveData.postValue(newUserList) }
    }
}