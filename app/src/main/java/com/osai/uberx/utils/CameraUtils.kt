package com.osai.uberx.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CameraUtils @Inject constructor(private val context: Context) {

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    /**
     * Using this for Google photos
     * https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app
     */
    fun getImagePathFromInputStreamUri(uri: Uri): String? {
        return if (uri.authority != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    createTemporalFileFrom(inputStream)?.path
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else null
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream): File? {
        var read: Int
        val buffer = ByteArray(8 * 1024)
        val targetFile: File? = createImageFile()
        val outputStream: OutputStream = FileOutputStream(targetFile)
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return targetFile
    }
}