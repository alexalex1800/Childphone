package com.example.stocksandbox.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.stocksandbox.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val photos by viewModel.photos.collectAsState()
    val scope = rememberCoroutineScope()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
        if (!granted) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.permission_denied))
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val cameraProvider = context.getCameraProvider()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar(exc.localizedMessage ?: "Camera error")
                }
            }
        }
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = context.getString(R.string.permission_camera_rationale))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(text = context.getString(R.string.request_permission_again))
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { previewView }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                viewModel.deleteAllPhotos { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) context.getString(R.string.delete_success)
                            else context.getString(R.string.delete_failure)
                        )
                    }
                }
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = context.getString(R.string.delete_all_photos))
            }
            Button(onClick = {
                takePhoto(
                    imageCapture = imageCapture,
                    executor = cameraExecutor,
                    file = viewModel.createPhotoFile(),
                    onSuccess = { file ->
                        viewModel.onPhotoCaptured(file)
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.capture_photo))
                        }
                    },
                    onError = {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.delete_failure))
                        }
                    }
                )
            }) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = context.getString(R.string.capture_photo))
            }
        }

        if (photos.isEmpty()) {
            Text(text = context.getString(R.string.gallery_empty))
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photos) { file ->
                    PhotoPreview(file = file)
                }
            }
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: ExecutorService,
    file: File,
    onSuccess: (File) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(file)
            }

            override fun onError(exception: ImageCaptureException) {
                file.delete()
                onError(exception)
            }
        }
    )
}

@Composable
private fun PhotoPreview(file: File) {
    val bitmapState = remember(file) { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(file) {
        bitmapState.value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(file.absolutePath)
        }
    }
    Card(
        modifier = Modifier.size(width = 120.dp, height = 160.dp)
    ) {
        val bitmap = bitmapState.value
        if (bitmap != null) {
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.loading_placeholder))
            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
    val future = ProcessCameraProvider.getInstance(this)
    future.addListener(
        {
            continuation.resume(future.get())
        },
        ContextCompat.getMainExecutor(this)
    )
}
