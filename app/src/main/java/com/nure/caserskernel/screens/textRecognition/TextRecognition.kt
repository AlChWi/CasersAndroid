package com.nure.caserskernel.screens.textRecognition

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nure.caserskernel.screens.carDetails.CarDetailsViewModel
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun TextRecognition(
    carDetailsViewModel: CarDetailsViewModel = viewModel(),
    navController: NavController,
    action: String,
    vehType: String,
    vehId: String,
    itemId: String
) {
    if(action.equals("add")) {
        carDetailsViewModel.onAppear(vehId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сканування бiрки") },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Image(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                }
            )
        },
    ) {
        TextRecognizer {
            if(!it.equals("")){
                if(action.equals("add")) {
                    if(vehType.equals("car")) {
                        carDetailsViewModel.addToCar(it)
                    } else if(vehType.equals("trailer")) {
                        carDetailsViewModel.addToTrailer(it)
                    }
                } else if(action.equals("verify")) {
                    carDetailsViewModel.verify(it)
                }
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun TextRecognizer(textRecognitionCompletion: (String) -> Unit) {
    val extractedText = remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CameraPreview(extractedText)
        Text(
            text = extractedText.value,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = { textRecognitionCompletion(extractedText.value) }) {
            Text("Додати")
        }
    }
}

@Composable
fun CameraPreview(extractedText: MutableState<String>) {

    val lifeCycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    val previewView = remember {
        PreviewView(context)
    }

    AndroidView(factory = { previewView }) {

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        ObjectDetectorImageAnalyzer(textRecognizer, extractedText)
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifeCycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CAMERA PREVIEW", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}

class ObjectDetectorImageAnalyzer(
    private val textRecognizer: TextRecognizer,
    private val extractedText: MutableState<String>
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(image)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val regex = """[a-zA-Z][0-9]{8}""".toRegex()
                        val matchResult = regex.find(it.result?.text ?: "")
                        val newText = matchResult?.value ?: ""
                        if(!newText.equals("")) {
                            extractedText.value = newText
                        }
                    }

                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    System.out.println(e)
                }
        }
    }
}