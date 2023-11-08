package com.example.storyappsubmission.ui.story.storycreate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyappsubmission.R
import com.example.storyappsubmission.customview.CustomPopUpAlert
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.databinding.ActivityStoryCreateBinding
import com.example.storyappsubmission.ui.GeneralViewModelFactory
import com.example.storyappsubmission.ui.main.MainActivity
import com.example.storyappsubmission.utils.reduceImageSize
import com.example.storyappsubmission.utils.tempFile
import com.example.storyappsubmission.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@SuppressLint("MissingPermission")
class StoryCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryCreateBinding
    private val viewModel: StoryCreateViewModel by viewModels { factory }
    private lateinit var factory: GeneralViewModelFactory

    private lateinit var path: String
    private var file: File? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var fusedLocation: FusedLocationProviderClient

    private val manageCameraIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(path)
            file = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.ivChosen.setImageBitmap(result)
        }
    }

    private val manageGalleryIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@StoryCreateActivity)
            file = myFile
            binding.ivChosen.setImageURI(selectedImg)
        }
    }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyCurrentLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyCurrentLocation()
                }
                permissions[Manifest.permission.CAMERA] ?: false -> {}
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false -> {}
                else -> {
                    Toast.makeText(this@StoryCreateActivity, R.string.permission_denied_alert, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = GeneralViewModelFactory.getInstance(binding.root.context)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        cameraCapture()
        chooseImageGallery()
        setupToolbar()
        getMyCurrentLocation()

        var latCoordinate: Double? = 0.0
        var lonCoordinate: Double? = 0.0

        binding.checkboxLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                latCoordinate = latitude
                lonCoordinate = latitude
            } else {
                latitude = 0.0
                latitude = 0.0
            }
        }

        binding.buttonPosting.setOnClickListener {
            val description = binding.descriptionEdit.text.toString()
            if (!isEmpty(description) && file != null && latCoordinate != null && lonCoordinate != null) {
                storyCreateStep(description, latCoordinate!!, lonCoordinate!!)
            } else {
                CustomPopUpAlert(this, R.string.error_create_story_alert).show()
            }
        }

    }


    override fun onResume() {
        super.onResume()
        binding.checkboxLocation.isChecked = false
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun cameraCapture() {
        binding.buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            tempFile(applicationContext).also {
                val photoURI: Uri = FileProvider.getUriForFile(this@StoryCreateActivity, "com.example.storyappsubmission.mycamera", it)
                path = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                manageCameraIntent.launch(intent)
            }
        }
    }

    private fun chooseImageGallery() {
        binding.buttonGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            manageGalleryIntent.launch(chooser)
        }
    }

    private fun storyCreateStep(desc: String, lat: Double, lon: Double) {
        val image = convertImage()
        val desc = plainDescription(desc)
        viewModel.createStoryPosting(image, desc, latitude!!, longitude!!).observe(this@StoryCreateActivity) { result ->
            if (result != null) {
                when(result) {
                    is ResultCondition.LoadingState -> {
                        progressLoading(true)
                    }
                    is ResultCondition.ErrorState -> {
                        progressLoading(false)
                        errorHandler(true)
                    }
                    is ResultCondition.SuccessState -> {
                        errorHandler(false)
                    }
                }
            }
        }
    }

    private fun plainDescription(description: String): RequestBody {
        return description.toRequestBody("text/plain".toMediaType())
    }

    private fun convertImage(): MultipartBody.Part {
        val file = reduceImageSize(file as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    private fun getMyCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkPermission(Manifest.permission.CAMERA) &&
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                } else {
                    Toast.makeText(
                        this@StoryCreateActivity,
                        getString(R.string.location_not_found_alert),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            )
        }
    }

    private fun errorHandler(error: Boolean) {
        if (error) {
            CustomPopUpAlert(this, R.string.error_message).show()
        } else {
            val alert = CustomPopUpAlert(this, R.string.success_create_story_alert)
            alert.show()
            alert.setOnDismissListener {
                homeRedirect()
            }

        }
    }

    private fun homeRedirect() {
        val intent = Intent(this@StoryCreateActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            hideNavbar(true)
            binding.progressBar.visibility = View.VISIBLE
            binding.createStoryLayout.visibility = View.GONE
        } else {
            hideNavbar(false)
            binding.progressBar.visibility = View.GONE
            binding.createStoryLayout.visibility = View.VISIBLE
        }
    }

    private fun hideNavbar(check: Boolean) {
        @Suppress("DEPRECATION")
        if(check) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.create_your_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }


}