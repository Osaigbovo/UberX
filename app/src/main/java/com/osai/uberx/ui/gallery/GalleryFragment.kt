package com.osai.uberx.ui.gallery

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.osai.uberx.R
import com.osai.uberx.UberXApp
import com.osai.uberx.utils.CameraUtils
import com.osai.uberx.utils.ViewModelFactory
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import javax.inject.Inject

class GalleryFragment : Fragment() {

    companion object {
        // Request code for location permission request.
        private const val REQUEST_CAPTURE_IMAGE = 10
        private const val PICK_IMAGE_REQUEST = 20
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var cameraUtils: CameraUtils
    private lateinit var imageView: ImageView

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

        imageView = root.findViewById<CircleImageView>(R.id.imageView)
        val profile_name = root.findViewById<EditText>(R.id.profile_name)
        val text_name = root.findViewById<TextView>(R.id.text_name)
        val openCamera = root.findViewById<Button>(R.id.openCamera)
        val openGallery = root.findViewById<Button>(R.id.openGallery)

        val userNameText = sharedPreferences.getString(getString(R.string.username), "")
        if (!userNameText.isNullOrEmpty()) text_name.text = userNameText

        val userImage = sharedPreferences.getString(getString(R.string.userimage), "")
        val imageUri = Uri.parse(userImage)
        if (imageUri != null) displaySelectedImage(imageUri)

        openCamera.setOnClickListener { openCamera() }
        openGallery.setOnClickListener { openGallery() }
        profile_name.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                text_name.text = profile_name.text.toString()
                galleryViewModel.saveUserName(profile_name.text.toString())
                profile_name.setText("")
                profile_name.requestFocus()
            }
            handled
        }
        return root
    }

    @SuppressLint("QueryPermissionsNeeded")
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
                    galleryViewModel.photoPathUri?.let {
                        displaySelectedImage(it)
                        galleryViewModel.saveUserImage(it)
                    }
                }
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        galleryViewModel.photoPath = cameraUtils.getImagePathFromInputStreamUri(uri)
                        displaySelectedImage(uri)
                        galleryViewModel.saveUserImage(uri)
                    }
                }
            }
        }
    }

    private fun displaySelectedImage(photoPathUri: Uri) {
        Glide
            .with(this@GalleryFragment)
            .load(photoPathUri.toString())
            .into(imageView)
    }
}
