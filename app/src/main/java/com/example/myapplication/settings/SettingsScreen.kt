package com.example.myapplication.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Home,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(8.dp)
        )

        Box(modifier = Modifier.weight(1f))

        SettingsMenuButton()
    }
}

@Composable
fun SettingsMenuButton() {
    val isMenuOpen = remember { mutableStateOf(false) }

    Box {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable { isMenuOpen.value = !isMenuOpen.value }
                .padding(8.dp)
        )

        DropdownMenu(
            expanded = isMenuOpen.value,
            onDismissRequest = { isMenuOpen.value = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("Profile", color = MaterialTheme.colorScheme.onSurface) },
                onClick = { isMenuOpen.value = false }
            )
            DropdownMenuItem(
                text = { Text("Settings", color = MaterialTheme.colorScheme.onSurface) },
                onClick = { isMenuOpen.value = false }
            )
            DropdownMenuItem(
                text = { Text("Sign Out", color = MaterialTheme.colorScheme.onSurface) },
                onClick = { isMenuOpen.value = false }
            )
        }
    }
}
