package com.osai.uberx.ui.gallery

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {}
    val text: LiveData<String> = _text

    var photoPath: String? = null
    var photoPathUri: Uri? = null

    val _photo = MutableLiveData<Uri>()
    val photo: LiveData<Uri> = _photo

    fun showSelectedImage(uri: Uri?) {
        _photo.value = uri
    }
}