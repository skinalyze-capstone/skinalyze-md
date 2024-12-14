    package com.dicoding.skinalyze.ui.home

    import android.Manifest
    import android.app.Activity.RESULT_OK
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
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
    import androidx.core.content.FileProvider
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import com.dicoding.skinalyze.ImageClassifierHelper
    import com.dicoding.skinalyze.R
    import com.dicoding.skinalyze.database.HistoryAnalyze
    import com.dicoding.skinalyze.database.HistoryAnalyzeRoom
    import com.dicoding.skinalyze.repository.HistoryAnalyzeRepository
    import com.dicoding.skinalyze.utils.uriToFile
    import com.yalantis.ucrop.UCrop
    import kotlinx.coroutines.launch
    import androidx.navigation.fragment.findNavController
    import java.io.File
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter

    @Suppress("DEPRECATION")
    class AnalyzeFragment : Fragment() {
        private lateinit var analyzeButton: Button
        private lateinit var ivFaceImagePreview: ImageView
        private var selectedImageFile: File? = null
        private var currentImageUri: Uri? = null
        private var imageClassifierHelper: ImageClassifierHelper? = null
        private lateinit var historyAnalyzeRepository: HistoryAnalyzeRepository
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
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(256, 256)
                .getIntent(requireContext())
            uCropLauncher.launch(uCropIntent)
        }

        private val uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    showCroppedImage(resultUri)
                } else {
                    showToast("Failed to get cropped result")
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                showToast("Cropping error: ${cropError?.message}")
            } else {
                if (currentImageUri != null) {
                    analyzeButton.isEnabled = true
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            imageClassifierHelper?.close()
            imageClassifierHelper = null
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
                classifyImage(currentImageUri!!)
            } else {
                showToast("Please select an image or take a picture first")
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == CAMERA_PERMISSION_REQUEST || requestCode == STORAGE_PERMISSION_REQUEST) {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
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

            val historyAnalyzeDao = HistoryAnalyzeRoom.getDatabase(requireContext()).historyAnalyzeDao()
            historyAnalyzeRepository = HistoryAnalyzeRepository(historyAnalyzeDao)

            imageClassifierHelper = ImageClassifierHelper(requireContext())
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
            }
        }

        private fun classifyImage(imageUri: Uri) {
            imageClassifierHelper?.classifyStaticImage(imageUri) { result ->
                if (result != null) {
                    val ingredients = when (result) {
                        "Acne" -> "Azelaic Acid, Glycolic Acid, Retinol, Salicylic Acid, Zinc PCA"
                        "Bags" -> "Niacinamide, Caffeine"
                        "Bopeng" -> "Ceramides, Retinol, Squalane"
                        "Bruntusan" -> "Zinc PCA, Tea Tree Oil, Azelaic Acid, Glycolic Acid, Salicylic Acid"
                        "Milia" -> "Vitamin C, Alpha Arbutin, AHA BHA, Niacinamide, Retinol"
                        "Redness" -> "Chamomile Extract, Centella Asiatica, Bisabolol, Avene Thermal Water"
                        else -> "Unknown Ingridiens"
                    }
                    val currentDateTime = LocalDateTime.now()
                    val date = currentDateTime.toLocalDate().toString()
                    val time = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    saveToHistory(result, ingredients, date, time)
                } else {
                    showToast("Failed to classify the image")
                }
            }
        }

        private fun saveToHistory(label: String, ingredients: String, date: String, time: String) {
            val imageUriString = currentImageUri?.toString() ?: ""
            val historyAnalyze = HistoryAnalyze(
                result = label,
                ingredients = ingredients,
                imageUri = imageUriString,
                date = date,
                time = time
            )

            lifecycleScope.launch {
                try {
                    val historyId = historyAnalyzeRepository.insertAndGetId(historyAnalyze)
                    if (historyId != -1L) {
                        navigateToResultFragment(historyId.toInt())
                    } else {
                        showToast("Failed to save history")
                    }
                } catch (e: Exception) {
                    showToast("Failed to save to history: ${e.message}")
                    Log.d("AnalyzeFragment", "Error saving to history: ${e.message}")
                }
            }
        }

        private fun navigateToResultFragment(historyId: Int) {
            val bundle = Bundle().apply {
                putInt("historyId", historyId)
            }
            findNavController().navigate(R.id.action_analyzeFragment_to_fragmentResult, bundle)
        }

        companion object {
            private const val CAMERA_PERMISSION_REQUEST = 1001
            private const val STORAGE_PERMISSION_REQUEST = 1002
        }
    }