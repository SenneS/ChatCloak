package be.senne.chatcloak

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

//image.image mag anders niet.
@SuppressLint("UnsafeOptInUsageError")
class QrScannerAnalyzer(var callback: (barcode : List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {
    private var lastAttempt = 0L

    override fun analyze(image: ImageProxy) {
        val currentAttempt = System.currentTimeMillis()
        //1 poging / 1 second?
        if(currentAttempt - lastAttempt >= 1000) {

            val barcodeScannerOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
            val barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)

            val imageImage = image.image
            if (imageImage != null) {
                val inputImage = InputImage.fromMediaImage(imageImage, image.imageInfo.rotationDegrees)
                barcodeScanner.process(inputImage).addOnSuccessListener { barcode ->
                    if(barcode.isNotEmpty()) {
                        callback(barcode)
                    }
                }.addOnCompleteListener {
                    image.close()
                }
            }
            lastAttempt = System.currentTimeMillis()
        } else {
            image.close()
        }
    }
}