package com.example.myapplication.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun InstitutionalEmailScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onConnect: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag("emailScreen")
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Connect your institution",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your institutional email to unlock papers from your library. This is optional.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Institutional email") },
            singleLine = true,
            modifier = Modifier
                .testTag("emailInput")
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onConnect,
            enabled = email.isNotBlank(),
            modifier = Modifier
                .testTag("btnConnect")
                .fillMaxWidth()
        ) {
            Text(text = "Connect")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .testTag("btnSkip")
                .fillMaxWidth()
        ) {
            Text(text = "Skip")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InstitutionalEmailScreenPreview() {
    MyApplicationTheme {
        InstitutionalEmailScreen(
            email = "",
            onEmailChange = {},
            onConnect = {},
            onSkip = {}
        )
    }
}
