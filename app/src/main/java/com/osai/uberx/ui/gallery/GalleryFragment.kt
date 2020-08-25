package com.osai.uberx.ui.gallery

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.osai.uberx.R
import com.osai.uberx.UberXApp
import com.osai.uberx.utils.CameraUtils
import com.osai.uberx.utils.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.File
import javax.inject.Inject

class GalleryFragment : Fragment() {

    companion object {
        //Request code for location permission request.
        private const val REQUEST_CAPTURE_IMAGE = 10
        private const val PICK_IMAGE_REQUEST = 20
    }

    @Inject lateinit var cameraUtils: CameraUtils
    @Inject lateinit var viewModelFactory: ViewModelFactory

    // Obtain the ViewModel - use the activity as the lifecycle owner
    private val galleryViewModel: GalleryViewModel by activityViewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        UberXApp.appComponent(requireActivity()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            text_name.text = it
        })

        val openCamera = root.findViewById<Button>(R.id.openCamera)
        val openGallery = root.findViewById<Button>(R.id.openGallery)
        openCamera.setOnClickListener { openCamera() }
        openGallery.setOnClickListener { openGallery() }

        return root
    }

    private fun openCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val file: File = cameraUtils.createImageFile()
            galleryViewModel.photoPath = file.absolutePath
            galleryViewModel.photoPathUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, galleryViewModel.photoPathUri)
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun openGallery() {
        val pickPhoto: Intent = Intent(Intent.ACTION_PICK)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        startActivityForResult(
            Intent.createChooser(pickPhoto, getText(R.string.select_picture)), PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {

                REQUEST_CAPTURE_IMAGE -> {
                    showSelectedImage(galleryViewModel.photoPathUri)
                }

                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        galleryViewModel.photoPath =
                            cameraUtils.getImagePathFromInputStreamUri(uri)
                        showSelectedImage(uri)
                    }
                }
            }
        }
    }

    private fun showSelectedImage(photoPathUri: Uri?) {
        Glide
            .with(this@GalleryFragment)
            .load(photoPathUri.toString())
            .into(imageView);
    }

}