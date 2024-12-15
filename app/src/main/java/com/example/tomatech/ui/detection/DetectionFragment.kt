package com.example.tomatech.ui.detection

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tomatech.R
import com.example.tomatech.databinding.FragmentDetectionBinding
import com.example.tomatech.ui.helper.ResultActivity
import com.example.tomatech.ui.helper.TFLiteHelper
import java.io.IOException

@Suppress("DEPRECATION")
class DetectionFragment : Fragment(R.layout.fragment_detection) {

    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetectionViewModel
    private lateinit var tfliteHelper: TFLiteHelper

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
        if (isSuccess) {
            showImage()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.currentImageUri = uri
        showImage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetectionBinding.bind(view)

        viewModel = ViewModelProvider(this)[DetectionViewModel::class.java]
        tfliteHelper = TFLiteHelper(requireContext())

        viewModel.currentImageUri?.let {
            showImage()
        }

        binding.galleryButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.cameraButton.setOnClickListener {
            openCamera()
        }

        binding.diagnoseButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun showImage() {
        try {
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                viewModel.currentImageUri?.let { uri ->
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, viewModel.currentImageUri)
            }
            binding.imagePreview.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Gagal memuat gambar")
        }
    }

    private fun analyzeImage() {
        if (viewModel.currentImageUri == null) {
            showToast("Pilih Gambar Terlebih Dahulu")
            return
        }

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, viewModel.currentImageUri)

            binding.progressIndicator.visibility = View.VISIBLE

            val result = tfliteHelper.classifyImage(bitmap)

            val label = result.first
            val confidenceScore = result.second

            binding.progressIndicator.visibility = View.GONE

            Log.d("ModelOutput", "Label: $label, Confidence Score: $confidenceScore")

            val confidencePercentage = (confidenceScore * 100).toInt()

            val diagnosis = "Diagnosis: $label\nConfidence: $confidencePercentage%"

            showToast(diagnosis)

            moveToResult(diagnosis, confidenceScore)

        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Silahkan Pilih Gambar Yang Valid")
        }
    }

    private fun openCamera() {
        viewModel.currentImageUri = createImageUri()
        Log.d("Diagnose", "Current Image Uri: ${viewModel.currentImageUri}")
        takePicture.launch(viewModel.currentImageUri)
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "temp_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun moveToResult(diagnosis: String, confidenceScore: Float) {
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra("RESULT", diagnosis)
        intent.putExtra("CONFIDENCE_SCORE", confidenceScore)
        intent.putExtra("IMAGE_URL", viewModel.currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}