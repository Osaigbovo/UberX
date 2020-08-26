package com.osai.uberx.ui.profile

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val sharedPreferences: SharedPreferences) :
    ViewModel() {

    var photoPath: String? = null
    var photoPathUri: Uri? = null

    fun saveUserName(name: String?) {
        with(sharedPreferences.edit()) {
            putString("userName", name)
            apply()
        }
    }

    fun saveUserImage(uri: Uri) {
        with(sharedPreferences.edit()) {
            putString("userImage", uri.toString())
            apply()
        }
    }
}
