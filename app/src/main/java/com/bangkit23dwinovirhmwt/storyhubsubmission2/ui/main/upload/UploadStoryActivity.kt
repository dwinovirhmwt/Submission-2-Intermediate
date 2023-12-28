package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bangkit23dwinovirhmwt.storyhubsubmission2.R
import com.bangkit23dwinovirhmwt.storyhubsubmission2.databinding.ActivityUploadStoryBinding
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.StoryHubModelFactory
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.camera.CameraActivity
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.camera.reduceFileImage
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.camera.uriToFile
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.Result

class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private val viewModel by viewModels<UploadStoryViewModel> {
        StoryHubModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestMediaPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_granted))
            } else {
                showToast(getString(R.string.permission_denied))
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }

                else -> {
                    showToast(getString(R.string.permission_denied))
                }
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestMediaPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setSupportActionBar(binding.toolbarUploadActivity)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initialForm()
        setFormData()
        backToMain()

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUploadImage.setOnClickListener { uploadStory() }
        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLastLocation()
            } else {
                viewModel.storyData.lat = 0.0
                viewModel.storyData.lon = 0.0
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getMyLocation(location)
                } else {
                    Toast.makeText(
                        this@UploadStoryActivity,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getMyLocation(location: Location) {
        viewModel.storyData.lat = location.latitude
        viewModel.storyData.lon = location.longitude
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private fun backToMain() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UploadStoryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()

            }
        })
    }

    private fun setFormData() {
        binding.edAddDescription.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.storyData.description = text.toString()
        }
    }

    private fun initialForm() {
        binding.edAddDescription.editText?.setText(viewModel.storyData.description)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.imgUploadStory.setImageURI(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadStory() {
        binding.btnUploadImage.isEnabled = false
        lifecycleScope.launch {
            if (viewModel.validate()) {
                showToast(getString(R.string.error_field_empty))
                binding.btnUploadImage.isEnabled = true
            } else {
                try {
                    currentImageUri?.let { uri ->
                        val imageFile = uriToFile(uri, this@UploadStoryActivity).reduceFileImage()

                        viewModel.addStory(imageFile).collect { result ->
                            when (result) {
                                is Result.Loading -> showLoading(true)
                                is Result.Success -> {
                                    showToast(result.data.message.toString())
                                    showLoading(false)
                                    binding.btnUploadImage.isEnabled = true

                                    val intent =
                                        Intent(this@UploadStoryActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    showLoading(false)
                                    binding.btnUploadImage.isEnabled = true
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    showToast(e.message.toString())
                    binding.btnUploadImage.isEnabled = true
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this@UploadStoryActivity, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}