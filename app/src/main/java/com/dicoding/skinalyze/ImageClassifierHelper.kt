package com.dicoding.skinalyze

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.dicoding.skinalyze.ml.ModelBaru
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

@Suppress("DEPRECATION")
class ImageClassifierHelper(private val context: Context) {
    private var model: ModelBaru? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        model = ModelBaru.newInstance(context)
    }

    fun classifyStaticImage(imageUri: Uri, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = uriToBitmap(imageUri)
                if (bitmap == null) {
                    withContext(Dispatchers.Main) {
                        callback(null) // Callback jika bitmap gagal dibuat
                    }
                    return@launch
                }

                // Mengonversi Bitmap menjadi TensorImage
                val tensorImage = TensorImage.fromBitmap(bitmap)

                // Memproses gambar menggunakan model
                val outputs = model?.process(tensorImage)

                // Mengecek dan mencetak daftar kategori dan skor
                outputs?.probabilityAsCategoryList?.forEach {
                    Log.d("Category", "Label: ${it.label}, Score: %.1f".format(it.score))
                }

                // Menemukan kategori dengan skor tertinggi
                val topCategory = outputs?.probabilityAsCategoryList
                    ?.maxByOrNull { it.score }

                // Mengambil label dari kategori dengan skor tertinggi
                val label = topCategory?.label

                // Mengembalikan hasil ke thread utama
                withContext(Dispatchers.Main) {
                    callback(label)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null) // Callback jika ada error
                }
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        model?.close()
        model = null
    }
}
