package com.bangkit.jajanjalanseller.ui.toko

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.jajanjalanseller.MainActivity
import com.bangkit.jajanjalanseller.R
import com.bangkit.jajanjalanseller.databinding.ActivityCreateTokoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@Suppress("DEPRECATION")
@AndroidEntryPoint
class CreateTokoActivity : AppCompatActivity() {

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


    }

    private fun createToko() {
        Log.d(TAG, "Creating Toko...")
        val name = createPartFromString(binding.edRegisterName.text.toString())
        val address = createPartFromString(binding.edRegisterAddres.text.toString())
        val phone = createPartFromString(binding.edRegisterPhone.text.toString())
        val description = createPartFromString(binding.edDescrption.text.toString())
        // Check if currentLocation is not null before using it
        val lat = currentLocation?.latitude
        val lon = currentLocation?.longitude
        val imageUrl =
            "https://kaltimtoday.co/wp-content/uploads/2022/04/Para-pedagang-Pasar-Induk-mengeluhkan-semakin-menjamur-pasar-tumpah-di-Sangatta-Kutim.-Ella-Kaltimtoday.jpeg"
        val imageRequestBody = imageUrl.toRequestBody("image/*".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", imageUrl, imageRequestBody)



viewModel.getUser().observe(this){
    viewModel.getUser().observe(this) { user ->
        Log.d(TAG, "User Token: ${user.token}")
        viewModel.createToko(
            user.token,
            name,
            address,
            phone,
            lat?.toFloat(),
            lon?.toFloat(),
            description,
            image
        )
    }
}
        viewModel.createStore.observe(this) { response ->
            binding.progressIndicator.show()
            if (response != null) {
                binding.progressIndicator.hide()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                binding.progressIndicator.show()
                AlertDialog.Builder(
                    this
                ).apply {
                    setTitle("Create Toko")
                    setMessage(getString(R.string.createtoko_fail))
                    create()
                    show()
                }
            }
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

    private fun createPartFromString(string: String): RequestBody {
        return string.toRequestBody(MultipartBody.FORM)
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


}




