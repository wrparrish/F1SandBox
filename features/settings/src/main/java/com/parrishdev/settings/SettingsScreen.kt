package com.parrishdev.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.parrishdev.navigation.SharedViewModel


@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Toggle Settings
            SettingsSection(title = "Toggle Settings") {
                ToggleSetting(
                    title = "Enable Notifications",
                    description = "Receive push notifications for important updates.",
                    initialValue = true
                )
                ToggleSetting(
                    title = "Dark Mode",
                    description = "Switch to a darker color scheme.",
                    initialValue = false
                )
            }

            // Checkbox Settings
            SettingsSection(title = "Checkbox Settings") {
                CheckboxSetting(
                    title = "Show Advanced Options",
                    description = "Display more technical settings.",
                    initialValue = false
                )
                CheckboxSetting(
                    title = "Enable Data Logging",
                    description = "Allow the app to collect usage data.",
                    initialValue = true
                )
            }
        }
    }

    @Composable
    fun SettingsSection(title: String, content: @Composable () -> Unit) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }

    @Composable
    fun ToggleSetting(title: String, description: String, initialValue: Boolean) {
        var isChecked by remember { mutableStateOf(initialValue) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isChecked = !isChecked }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(checked = isChecked, onCheckedChange = { isChecked = it })
        }
    }

    @Composable
    fun CheckboxSetting(title: String, description: String, initialValue: Boolean) {
        var isChecked by remember { mutableStateOf(initialValue) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isChecked = !isChecked }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
        }
    }
fun NavGraphBuilder.settingsGraph(sharedViewModel: SharedViewModel) {
    navigation(startDestination = Routes.Settings.SCREEN, route = Routes.Settings.GRAPH) {
        composable(Routes.Settings.SCREEN) {
            sharedViewModel.updateTitle("Settings")
            SettingsScreen()
        }
    }
}