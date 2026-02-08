package com.parrishdev.settings.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.parrishdev.ui.F1Red
import com.parrishdev.ui.F1SandboxTheme
import com.parrishdev.ui.GhostGrey

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SettingsEvent.ShowSettingChanged -> {}
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
        SettingsSection(title = "PREFERENCES") {
            ToggleSetting(
                title = "Notifications",
                description = "Receive push notifications for race updates",
                isChecked = viewState.notificationsEnabled,
                onCheckedChange = onNotificationsToggled
            )
            ToggleSetting(
                title = "Dark Mode",
                description = "Use dark color scheme",
                isChecked = viewState.darkModeEnabled,
                onCheckedChange = onDarkModeToggled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(title = "ADVANCED") {
            CheckboxSetting(
                title = "Advanced Options",
                description = "Display technical settings and diagnostics",
                isChecked = viewState.showAdvancedOptions,
                onCheckedChange = onAdvancedOptionsToggled
            )
            CheckboxSetting(
                title = "Data Logging",
                description = "Allow anonymous usage data collection",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(14.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = F1Red,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
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
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = GhostGrey,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = F1Red,
                uncheckedThumbColor = GhostGrey,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = GhostGrey,
            )
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp,
    )
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
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = GhostGrey,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = F1Red,
                uncheckedColor = GhostGrey,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
private fun SettingsScreenContentPreview() {
    F1SandboxTheme(darkTheme = true) {
        SettingsScreenContent(
            viewState = SettingsViewState(
                notificationsEnabled = true,
                darkModeEnabled = true,
                showAdvancedOptions = false,
                dataLoggingEnabled = true
            ),
            onNotificationsToggled = {},
            onDarkModeToggled = {},
            onAdvancedOptionsToggled = {},
            onDataLoggingToggled = {}
        )
    }
}
