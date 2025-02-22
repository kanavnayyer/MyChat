package com.awesome.mychat.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesome.mychat.R
import com.awesome.mychat.databinding.FragmentCameraBinding

import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class CameraFragment : Fragment() {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var preview: Preview
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var binding: FragmentCameraBinding
    private var capturedImageUri: Uri? = null

    private val args: CameraFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera()

        binding.swapCameraButton.setOnClickListener {
            swapCamera()
        }

        binding.captureButton.setOnClickListener {
            captureImage()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e(getString(R.string.camerafragment), getString(R.string.camera_binding_failed), e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun swapCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun captureImage() {
        val photoFile = File.createTempFile(
            getString(R.string.temp_image),
            getString(R.string.jpg), requireContext().cacheDir)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    capturedImageUri = Uri.fromFile(photoFile)
                    navigateToPreviewScreen(capturedImageUri!!)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(getString(R.string.camerafragment),
                        getString(R.string.image_capture_failed, exception.message), exception)
                }
            })
    }

    private fun navigateToPreviewScreen(imageUri: Uri) {
        val action = CameraFragmentDirections.actionCameraFragmentToPreviewFragment(
            imageUri = imageUri.toString(),
            user = args.user
        )
        findNavController().navigate(action)
    }

}
