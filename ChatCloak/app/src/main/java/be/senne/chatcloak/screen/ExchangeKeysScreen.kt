package be.senne.chatcloak.screen

import android.Manifest
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.senne.chatcloak.KeyContainer
import be.senne.chatcloak.QrScannerAnalyzer
import be.senne.chatcloak.viewmodel.ExchangeKeysVM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun createExchangeKeysScreen(nav: NavController, vm : ExchangeKeysVM = viewModel()) {
    val tabs = listOf("public_key", "scanner")
    val tabIdx = remember { mutableStateOf(0) }
    val nextEnabled = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Exchange Keys")
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    val kc = KeyContainer(vm.key, vm.publicKey)
                    nav.navigate("establish_connection_screen/$kc")
                                 }, enabled = nextEnabled.value) {
                    Text("Next")
                }
            }
        })

    }, content = { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            TabRow(selectedTabIndex = tabIdx.value, Modifier.height(40.dp)) {
                tabs.forEachIndexed { index, s ->
                    Tab(selected = index == tabIdx.value, onClick = { tabIdx.value = index }) {
                        Text(text = s, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            when(tabs[tabIdx.value]) {
                "public_key" -> {
                    nextEnabled.value = vm.publicKeyHasBeenObtained
                    createPublicKey(vm)
                }
                "scanner" -> {
                    nextEnabled.value = vm.publicKeyHasBeenObtained
                    createScanner(vm, nextEnabled)
                }
            }
        }
    })
}


@Composable
private fun createPublicKey(vm : ExchangeKeysVM) {
    val bitmap = remember { mutableStateOf(vm.generateQrCode()) }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Your public key:", fontSize = 30.sp, textAlign = TextAlign.Center)
        
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(bitmap = bitmap.value, contentDescription = "public_key", Modifier.clickable(onClick = {
                if(!vm.publicKeyHasBeenObtained) {
                    vm.refreshKeyPair()
                    bitmap.value = vm.generateQrCode()
                }
            }))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun createScanner(vm : ExchangeKeysVM, buttonEnabled : MutableState<Boolean>) {
    var publicKeyHasBeenObtained = remember { mutableStateOf(vm.publicKeyHasBeenObtained) }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        if(!publicKeyHasBeenObtained.value) {
            Text(
                text = "Scan your friend's public key:",
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        }

        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

        if(!cameraPermissionState.status.isGranted) {
            Column() {
                Text("Ik heb camera permissie nodig om QR codes te scannen.")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(text = "Geef Camera Permissie")
                }
            }
        }
        else {
            if(publicKeyHasBeenObtained.value) {
                Text("Your friend's public key has been scanned, once he has scanned yours press next. (make sure not to refresh the keys while this is in progress)")
            } else {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val cameraProviderFuture = remember {
                    ProcessCameraProvider.getInstance(context)
                }

                AndroidView(factory = { viewContext ->
                    val previewView = PreviewView(viewContext)
                    val executor = ContextCompat.getMainExecutor(viewContext)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also { preview ->
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.Builder().requireLensFacing(
                            CameraSelector.LENS_FACING_BACK).build()
                        val qrScanner = QrScannerAnalyzer { barcode ->
                            for (qr in barcode) {
                                val qrstr = qr.displayValue
                                if (qrstr != null) {
                                    if(qrstr.startsWith("CLOAK:")){
                                        Toast.makeText(
                                            context,
                                            "Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        publicKeyHasBeenObtained.value = true
                                        vm.publicKeyHasBeenObtained = true
                                        vm.publicKey = qrstr.substring(6)
                                        buttonEnabled.value = true
                                    }
                                    else {
                                        Toast.makeText(
                                            context,
                                            "Scanned Qr isn't a CloakChat QR Code",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                        val imageAnalysis : ImageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also { ia ->
                            ia.setAnalyzer(executor, qrScanner)
                        }

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)

                    }, executor)
                    previewView
                }, Modifier.fillMaxSize())
            }
        }

    }
}