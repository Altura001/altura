package com.example.ultra.auth.presentation.auth.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.auth.presentation.intent.AuthAction
import com.example.ultra.auth.presentation.intent.AuthEvent
import com.example.ultra.auth.presentation.intent.AuthMode
import com.example.ultra.auth.presentation.intent.AuthState
import com.example.ultra.auth.presentation.intent.SignupAccountType
import com.example.ultra.auth.presentation.viewmodel.AuthViewModel
import com.example.ultra.core.presentation.ObserveAsEvents
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import ultra.shared.generated.resources.Res
import ultra.shared.generated.resources.altura_logo

private val ScreenBackground = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFD7D4D6)
private val LabelColor = Color(0xFF4D5054)
private val InputColor = Color(0xFF1F242B)
private val AccentOrange = Color(0xFFFF6300)
private val AccentBlue = Color(0xFF1F69C1)
private val TermsColor = Color(0xFFD8B000)
private val LinkBlue = Color(0xFF0D2F95)
private val SocialBackground = Color(0xFFF2ECEE)
private val DividerColor = Color(0xFFE2DFE0)

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AuthEvent.NavigateToHome -> onLoginSuccess()
        }
    }

    AuthScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun AuthScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (state.mode == AuthMode.Login) "Welcome to" else "",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF17A118),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(if (state.mode == AuthMode.Login) 16.dp else 8.dp))

        AlturaLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(28.dp))

        if (state.mode == AuthMode.Login) {
            LoginContent(state, onAction)
        } else {
            SignupContent(state, onAction)
        }
    }
}

@Composable
private fun LoginContent(state: AuthState, onAction: (AuthAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AccountTypeSwitcher(
            selected = state.accountType,
            onSelect = { onAction(AuthAction.OnAccountTypeChange(it)) }
        )

        Text(
            text = if (state.accountType == SignupAccountType.Vendor) {
                "Login to manage your vendor store"
            } else {
                "Login to continue shopping"
            },
            color = LabelColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        AuthField(
            label = "Phone Number or Email",
            value = state.email,
            onValueChange = { onAction(AuthAction.OnEmailChange(it)) },
            leadingIcon = Icons.Outlined.Person,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthField(
            label = "Password",
            value = state.password,
            onValueChange = { onAction(AuthAction.OnPasswordChange(it)) },
            trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
            onTrailingClick = { onAction(AuthAction.OnPasswordVisibilityToggle) },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        Text(
            text = "Forgot Password",
            color = TermsColor,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 12.dp)
        )

        if (state.error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryActionButton(
            text = "Login",
            background = AccentOrange,
            enabled = state.email.isNotBlank() && state.password.isNotBlank() && !state.isLoading,
            loading = state.isLoading,
            onClick = { onAction(AuthAction.OnLoginClick) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        TermsBlock()

        Spacer(modifier = Modifier.height(20.dp))

        OrDivider()

        Spacer(modifier = Modifier.height(20.dp))

        SocialButton(
            label = "CONTINUE WITH FACEBOOK",
            badgeColor = Color(0xFF1877F2),
            badgeText = "f",
            onClick = { onAction(AuthAction.OnAppleSignIn("facebook_mock")) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        SocialButton(
            label = "CONTINUE WITH GOOGLE ACCOUNT",
            badgeColor = Color(0xFFDB4437),
            badgeText = "g+",
            onClick = { onAction(AuthAction.OnGoogleSignIn("google_mock")) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Don`t have an account",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Create Account",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onAction(AuthAction.OnModeChange(AuthMode.Signup)) },
            style = MaterialTheme.typography.headlineSmall,
            color = LinkBlue,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SignupContent(state: AuthState, onAction: (AuthAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            color = AccentOrange,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        AccountTypeSwitcher(
            selected = state.accountType,
            onSelect = { onAction(AuthAction.OnAccountTypeChange(it)) }
        )

        Text(
            text = if (state.accountType == SignupAccountType.Vendor) {
                "Create a vendor account to sell on Altura Nova"
            } else {
                "Create a customer account to shop on Altura Nova"
            },
            color = LabelColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        AuthField(
            label = "Frist Name",
            value = state.firstName,
            onValueChange = { onAction(AuthAction.OnFirstNameChange(it)) },
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthField(
            label = "Surname",
            value = state.surname,
            onValueChange = { onAction(AuthAction.OnSurnameChange(it)) },
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state.accountType == SignupAccountType.Vendor) {
            AuthField(
                label = "Store Name",
                value = state.storeName,
                onValueChange = { onAction(AuthAction.OnStoreNameChange(it)) },
                leadingIcon = Icons.Outlined.Store
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        AuthField(
            label = "Email",
            value = state.email,
            onValueChange = { onAction(AuthAction.OnEmailChange(it)) },
            leadingIcon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthField(
            label = "Phone Number",
            value = state.phoneNumber,
            onValueChange = { onAction(AuthAction.OnPhoneNumberChange(it)) },
            leadingIcon = Icons.Outlined.Phone,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthField(
            label = "Date  of Birth",
            value = state.dateOfBirth,
            onValueChange = { onAction(AuthAction.OnDateOfBirthChange(it)) },
            trailingIcon = Icons.Outlined.CalendarMonth,
            hint = "yyyy / mm / dd",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(12.dp))

        GenderField(
            selected = state.gender,
            onSelect = { onAction(AuthAction.OnGenderChange(it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthField(
            label = "Password",
            value = state.password,
            onValueChange = { onAction(AuthAction.OnPasswordChange(it)) },
            trailingIcon = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
            onTrailingClick = { onAction(AuthAction.OnPasswordVisibilityToggle) },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthField(
            label = "Re-enter Password",
            value = state.confirmPassword,
            onValueChange = { onAction(AuthAction.OnConfirmPasswordChange(it)) },
            trailingIcon = if (state.isConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
            onTrailingClick = { onAction(AuthAction.OnConfirmPasswordVisibilityToggle) },
            visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        if (state.error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        TermsBlock()

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryActionButton(
            text = if (state.accountType == SignupAccountType.Vendor) "Create Vendor Account" else "Signup",
            background = AccentBlue,
            enabled = !state.isLoading && state.password.isNotBlank() && state.confirmPassword.isNotBlank(),
            loading = state.isLoading,
            onClick = { onAction(AuthAction.OnSignupClick) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        OrDivider()

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Already have an account?",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Login",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onAction(AuthAction.OnModeChange(AuthMode.Login)) },
            style = MaterialTheme.typography.headlineSmall,
            color = AccentBlue,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AccountTypeSwitcher(
    selected: SignupAccountType,
    onSelect: (SignupAccountType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AccountTypeChip(
            text = "Customer",
            selected = selected == SignupAccountType.Customer,
            onClick = { onSelect(SignupAccountType.Customer) },
            modifier = Modifier.weight(1f)
        )
        AccountTypeChip(
            text = "Vendor",
            selected = selected == SignupAccountType.Vendor,
            onClick = { onSelect(SignupAccountType.Vendor) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccountTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) AccentBlue else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else InputColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    hint: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            color = LabelColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (hint != null) {
                    Text(hint, color = InputColor.copy(alpha = 0.85f))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ScreenBackground,
                unfocusedContainerColor = ScreenBackground,
                focusedBorderColor = BorderColor,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = InputColor,
                unfocusedTextColor = InputColor,
                focusedLeadingIconColor = InputColor,
                unfocusedLeadingIconColor = InputColor,
                focusedTrailingIconColor = InputColor,
                unfocusedTrailingIconColor = InputColor
            ),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(imageVector = it, contentDescription = null)
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    if (onTrailingClick != null) {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.clickable { onTrailingClick() }
                        )
                    } else {
                        Icon(imageVector = it, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        )
    }
}

@Composable
private fun GenderField(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected.ifBlank { "Select Gender" }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Gender",
            style = MaterialTheme.typography.headlineSmall,
            color = LabelColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = display,
                    style = MaterialTheme.typography.titleLarge,
                    color = InputColor
                )
                Text(text = "⌄", style = MaterialTheme.typography.headlineLarge)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Male", "Female", "Prefer not to say").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    background: Color,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TermsBlock() {
    Text(
        text = "By continuing you agree to Altura Nova",
        style = MaterialTheme.typography.titleLarge,
        color = InputColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = "Terms and Conditions",
        style = MaterialTheme.typography.titleLarge,
        color = TermsColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(DividerColor)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(42.dp)
                .background(Color(0xFFE5E5E8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("or", style = MaterialTheme.typography.titleLarge)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(DividerColor)
        )
    }
}

@Composable
private fun SocialButton(
    label: String,
    badgeColor: Color,
    badgeText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(SocialBackground, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF131313),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AlturaLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.altura_logo),
        contentDescription = "Altura logo",
        modifier = modifier
            .width(200.dp)
            .height(141.dp),
        contentScale = ContentScale.Fit
    )
}
