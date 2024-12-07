package com.dicoding.skinalyze.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.app.Activity.RESULT_OK
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.utils.uriToFile
import java.io.File

@Suppress("DEPRECATION")
class AnalyzeFragment : Fragment() {

    private lateinit var analyzeButton: Button
    private lateinit var ivFaceImagePreview: ImageView
    private var selectedImageFile: File? = null
    private var currentImageUri: Uri? = null

    // Deklarasi Launcher Gallery
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            val myFile = uriToFile(uri, requireContext())
            selectedImageFile = myFile
            ivFaceImagePreview.setImageURI(uri)
            openCropActivity(uri)
        } else {
            showToast("Failed to select image")
        }
    }

    // Deklarasi Launcher Kamera
    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            currentImageUri?.let { uri ->
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val tempFile = File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    if (tempFile.exists()) {
                        selectedImageFile = tempFile
                        ivFaceImagePreview.setImageURI(Uri.fromFile(tempFile))
                        openCropActivity(Uri.fromFile(tempFile))
                    } else {
                        showToast("Image file not found")
                    }
                } ?: showToast("Failed to read image URI")
            }
        } else {
            showToast("Failed to take picture")
        }
    }

    private fun openCropActivity(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg"))
        val uCropIntent = UCrop.of(uri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1080, 1080)
            .getIntent(requireContext())
        uCropLauncher.launch(uCropIntent)
    }

    // Penanganan hasil dari UCrop
    private val uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                // Menampilkan gambar hasil crop dan mengaktifkan tombol Analyze
                showCroppedImage(resultUri)
            } else {
                showToast("Failed to get cropped result")
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            showToast("Cropping error: ${cropError?.message}")
        } else {
            // Jika crop dibatalkan (menekan tombol X atau back), cek apakah ada gambar di ImageView
            if (currentImageUri != null) {
                // Gambar masih ada di ImageView, aktifkan tombol Analyze
                analyzeButton.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analyze, container, false)
    }

    private fun showCroppedImage(uri: Uri) {
        val imageView: ImageView = requireView().findViewById(R.id.iv_face_image_preview)
        imageView.setImageURI(uri)
        currentImageUri = uri

        analyzeButton.isEnabled = currentImageUri != null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST)

        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST)
        }
    }

    private fun handleAnalyzeButtonClick() {
        if (currentImageUri != null) {
            showToast("Continuing image analysis...")
        } else {
            showToast("Please select an image or take a picture first")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST || requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Tampilkan Toast bahwa izin telah diberikan
                showToast("Permissions granted. You can now use the camera or gallery.")
            } else {
                showToast("Permissions are required to take pictures and access the gallery.")
            }
        }
    }

    private fun openCamera() {
        val photoFile = File(requireContext().cacheDir, "temp.jpg")
        val photoUri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        currentImageUri = photoUri
        launcherCamera.launch(photoUri)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analyzeButton = view.findViewById(R.id.button_analyze)
        ivFaceImagePreview = view.findViewById(R.id.iv_face_image_preview)
        analyzeButton.isEnabled = false

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_face_scan)
        drawable?.let {
            val color = ContextCompat.getColor(requireContext(), R.color.gray_500)
            drawable.setTint(color)
            analyzeButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }

        view.findViewById<Button>(R.id.bt_open_gallery).setOnClickListener {
            if (checkPermissions()) {
                openGallery()
            } else {
                requestPermissions()
            }
        }

        view.findViewById<Button>(R.id.bt_open_camera).setOnClickListener {
            if (checkPermissions()) {
                openCamera()
            } else {
                requestPermissions()
            }
        }

        analyzeButton.setOnClickListener {
            handleAnalyzeButtonClick()
            showToast("Analyze button pressed")
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 1001
        private const val STORAGE_PERMISSION_REQUEST = 1002
    }
}