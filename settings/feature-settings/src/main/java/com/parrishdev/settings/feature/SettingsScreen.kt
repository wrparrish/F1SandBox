package com.parrishdev.settings.feature

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Settings screen displaying app configuration options.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SettingsEvent.ShowSettingChanged -> {
                    // Could show snackbar here
                }
            }
        }
    }

    SettingsScreenContent(
        viewState = viewState,
        onNotificationsToggled = viewModel::onNotificationsToggled,
        onDarkModeToggled = viewModel::onDarkModeToggled,
        onAdvancedOptionsToggled = viewModel::onAdvancedOptionsToggled,
        onDataLoggingToggled = viewModel::onDataLoggingToggled
    )
}

@Composable
private fun SettingsScreenContent(
    viewState: SettingsViewState,
    onNotificationsToggled: (Boolean) -> Unit,
    onDarkModeToggled: (Boolean) -> Unit,
    onAdvancedOptionsToggled: (Boolean) -> Unit,
    onDataLoggingToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
                isChecked = viewState.notificationsEnabled,
                onCheckedChange = onNotificationsToggled
            )
            ToggleSetting(
                title = "Dark Mode",
                description = "Switch to a darker color scheme.",
                isChecked = viewState.darkModeEnabled,
                onCheckedChange = onDarkModeToggled
            )
        }

        // Checkbox Settings
        SettingsSection(title = "Checkbox Settings") {
            CheckboxSetting(
                title = "Show Advanced Options",
                description = "Display more technical settings.",
                isChecked = viewState.showAdvancedOptions,
                onCheckedChange = onAdvancedOptionsToggled
            )
            CheckboxSetting(
                title = "Enable Data Logging",
                description = "Allow the app to collect usage data.",
                isChecked = viewState.dataLoggingEnabled,
                onCheckedChange = onDataLoggingToggled
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
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
private fun ToggleSetting(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun CheckboxSetting(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {
    SettingsScreenContent(
        viewState = SettingsViewState(
            notificationsEnabled = true,
            darkModeEnabled = false,
            showAdvancedOptions = false,
            dataLoggingEnabled = true
        ),
        onNotificationsToggled = {},
        onDarkModeToggled = {},
        onAdvancedOptionsToggled = {},
        onDataLoggingToggled = {}
    )
}
