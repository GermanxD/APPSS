package app.cui.ro.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cui.ro.R
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun DataColumn(
    imageResId: Int,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(CuiroColors.SecondaryRose.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = CuiroColors.FontBrown,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CustomTopAppBar(
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    title: String
) {
    // Obtener el padding de la status bar usando las APIs nativas
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Spacer para la status bar
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
        )

        // TopAppBar principal
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                        IconButton(onClick = onNotificationsClick) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Notificaciones",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_general),
                        contentDescription = "Logo",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            },
            backgroundColor = CuiroColors.ObjectsPink,
            contentColor = Color.Black,
            navigationIcon = null,
            actions = {}
        )
    }
}

@Composable
fun CenteredText(text: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text,
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        )
    }
}

@Composable
fun GenderButton(gender: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) app.cui.ro.ui.theme.CuiroColors.ObjectsPink else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray // Cambiar color del texto.
        ),
        border = if (!isSelected) BorderStroke(
            1.dp,
            Color.Gray
        ) else null // Opcional: agregar un borde para resaltar.
    ) {
        Text(text = gender)
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLength: Int? = null
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {
                if (maxLength == null || it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            label = { Text(label) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = CuiroColors.PinkFields,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = CuiroColors.FontBrown,
                focusedLabelColor = CuiroColors.FontBrown,
            ),
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (maxLength != null) {
            Text(
                text = "${value.length} / $maxLength", style =
                MaterialTheme.typography.caption.copy(
                    fontSize =
                    12.sp
                ), color =
                if (value.length > maxLength) Color.Red else Color.White, modifier =
                Modifier.align(Alignment.End)
            )
        }
    }
}