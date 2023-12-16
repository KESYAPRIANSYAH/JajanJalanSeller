package com.bangkit.jajanjalanseller.ui.toko

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.jajanjalanseller.MainActivity
import com.bangkit.jajanjalanseller.data.Result
import com.bangkit.jajanjalanseller.databinding.ActivityCreateTokoBinding
import com.bangkit.jajanjalanseller.utils.reduceFileImage
import com.bangkit.jajanjalanseller.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CreateTokoActivity : AppCompatActivity() {
    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityCreateTokoBinding
    private val viewModel: CreateTokoViewModel by viewModels()
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {

                showPermissionDeniedDialog()


            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        binding.btnConfirm.setOnClickListener {
            createToko()
        }
        binding.btnUplpad.setOnClickListener { startGallery() }

    }

    private fun createToko() {
        currentImageUri?.let { uri ->

            val name = binding.edRegisterName.text.toString()
            val address = binding.edRegisterAddres.text.toString()
            val phone = binding.edRegisterPhone.text.toString()
            val description = binding.edDescrption.text.toString()
            val lat = currentLocation?.latitude
            val lon = currentLocation?.longitude
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaType())
            val image = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )




            viewModel.createToko(
                name,
                address,
                phone,
                lat?.toFloat(),
                lon?.toFloat(),
                description,
                image
            )

            viewModel.createStore.observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressIndicator.hide()
                        // Navigasi ke MainActivity
                        navigateToMainActivity()
                    }

                    is Result.Error -> {
                        binding.progressIndicator.hide()
                        result.message?.let { showErrorDialog(it) }
                    }

                    is Result.Loading -> {
                        binding.progressIndicator.show()
                    }

                    else -> {}
                }
            }


        }

    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog(errorMessage: String) {
        Toast.makeText(this, "Daftar Toko Gagal;", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(this).apply {
            setTitle("Gagal Membuat Lapak")
            setMessage(errorMessage)
            create()
            show()
        }
    }

    private fun checkLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                requestLocationUpdates()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000) // Update interval in milliseconds

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    currentLocation = location
                    Log.d(TAG, "Current Location: $currentLocation")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



    companion object {
        private const val TAG = "CreateTokoActivity"


    }


    private fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission Required")
        builder.setMessage("Please grant location permission to use this feature.")
        builder.setPositiveButton("Open Settings") { dialog, _ ->
            openAppSettings()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {

            currentImageUri = uri
            showNameImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showNameImage() {
        currentImageUri?.let { uri ->
            val imageName = getFileName(uri)
            Log.d("Image URI", "showImage: $uri")
            binding.tvStoreImage.text = "Uploaded Image: $imageName"
            binding.tvStoreImage.visibility = View.VISIBLE
        }
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        } ?: "Unknown"
    }

}

