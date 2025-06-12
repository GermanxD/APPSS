package app.cui.ro.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMProfileImage
import app.cui.ro.ui.theme.CuiroColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NavBarScreenProfile(
    authService: AuthService,
) {
    val userId = remember { authService.getUserId() }
    var userData by remember {
        mutableStateOf<Map<String, String?>>(
            mapOf(
                "fullname" to "Usuario",
                "username" to "usuario",
                "email" to "",
                "gender" to "",
                "birthDate" to ""
            )
        )
    }

    val vmProfileImage: VMProfileImage = viewModel()

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllUserData(userId) { data ->
                userData = data
            }
            vmProfileImage.loadProfileImageFromFirestore(userId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState())
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            // Card principal con imagen y nombre
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    userId?.let {
                        ProfileScreen(
                            userId = it,
                            vmProfileImage = vmProfileImage
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userData["fullname"] ?: "Usuario",
                        style = MaterialTheme.typography.h6.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "@${userData["username"]?.lowercase() ?: ""}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
            }

            // Card de información de contacto
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Información de Contacto",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo Electrónico",
                        value = userData["email"]
                    )
                }
            }

            // Card de información personal
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Información Personal",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.DateRange,
                        label = "Fecha de Nacimiento",
                        value = userData["birthDate"]
                    )

                    if (!userData["birthDate"].isNullOrEmpty() && !userData["gender"].isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Género",
                        value = userData["gender"]
                    )
                }
            }

            // Card de acciones (opcional)
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Acciones",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = { /* Acción para editar perfil */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = CuiroColors.ObjectsPink
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editar Perfil",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String?) {
    if (!value.isNullOrEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CuiroColors.ObjectsPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userId: String,
    vmProfileImage: VMProfileImage,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val base64Image by vmProfileImage.profileImage

    val context = LocalContext.current
    var uploadState by remember { mutableStateOf(VMProfileImage.ImageUploadState.IDLE) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri
            val fileSizeInBytes = vmProfileImage.getFileSize(selectedUri, context)
            val maxFileSizeInBytes = 3000 * 1024

            if (fileSizeInBytes > maxFileSizeInBytes) {
                uploadState = VMProfileImage.ImageUploadState.SIZE_EXCEEDED
                errorMessage = "La imagen excede el tamaño máximo permitido (3MB)."
                dialogMessage = errorMessage
                isSuccess = false
                showDialog = true
                scope.launch {
                    delay(1000)
                    showDialog = false
                }
            } else {
                uploadState = VMProfileImage.ImageUploadState.UPLOADING
                errorMessage = ""
                CoroutineScope(Dispatchers.IO).launch {
                    val base64 = vmProfileImage.convertImageToBase64(selectedUri, context)
                    withContext(Dispatchers.Main) {
                        vmProfileImage.saveImageToFirestore(userId, base64,
                            onSuccess = {
                                uploadState = VMProfileImage.ImageUploadState.SUCCESS
                                dialogMessage = "Imagen actualizada con éxito!"
                                isSuccess = true
                                showDialog = true
                                scope.launch {
                                    delay(1000)
                                    showDialog = false
                                }
                            },
                            onFailure = {
                                uploadState = VMProfileImage.ImageUploadState.ERROR
                                errorMessage = "Error al guardar la imagen. Intenta de nuevo."
                                dialogMessage = errorMessage
                                isSuccess = false
                                showDialog = true
                                scope.launch {
                                    delay(1000)
                                    showDialog = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.size(80.dp)) {
        if (!base64Image.isNullOrEmpty()) {
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") }
            )
        } else {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") }
            )
        }

        if (uploadState == VMProfileImage.ImageUploadState.UPLOADING) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                strokeWidth = 4.dp,
                color = MaterialTheme.colors.primary
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    backgroundColor = CuiroColors.SecondaryRose,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val icon =
                                if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Close
                            val tint = if (isSuccess) Color.Green else Color.Red
                            Icon(
                                imageVector = icon,
                                contentDescription = if (isSuccess) "Éxito" else "Error",
                                tint = tint,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = dialogMessage,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

