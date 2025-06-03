package app.cui.ro.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

class VMProfileImage : ViewModel() {

    private val _profileImage = mutableStateOf<String?>(null)
    val profileImage: State<String?> = _profileImage

    enum class ImageUploadState {
        IDLE,
        UPLOADING,
        SUCCESS,
        ERROR,
        SIZE_EXCEEDED
    }

    fun getFileSize(uri: Uri, context: Context): Long {
        val contentResolver = context.contentResolver
        var fileSize = 0L
        contentResolver.openInputStream(uri)?.use { inputStream ->
            fileSize = inputStream.available().toLong()
        }
        return fileSize
    }

    fun convertImageToBase64(imageUri: Uri, context: Context): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val byteArrayOutputStream = ByteArrayOutputStream()
        // Comprime la imagen (ajusta la calidad según tus necesidades)
        bitmap?.compress(
            Bitmap.CompressFormat.JPEG,
            70,
            byteArrayOutputStream
        )  // Reduce la calidad a 70%
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveImageToFirestore(
        userId: String,
        base64Image: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("users")
            .document(userId)
            .update("profileImage", base64Image)
            .addOnSuccessListener {
                println("Imagen actualizada correctamente en Firestore")
                _profileImage.value = base64Image
                onSuccess() // Ejecutar el callback después de guardar
            }
            .addOnFailureListener { e ->
                println("Error al actualizar la imagen: ${e.message}")
                onFailure()
            }
    }

    fun loadProfileImageFromFirestore(userId: String) {
        val db = Firebase.firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                _profileImage.value = if (doc.exists()) doc.getString("profileImage") else null
            }
            .addOnFailureListener {
                _profileImage.value = null
            }
    }
}