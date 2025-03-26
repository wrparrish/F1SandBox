package com.parrishdev.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.parrishdev.model.Driver

@Composable
fun DriverHeadshot(driver: Driver, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = driver.headshotUrl,
        contentDescription = "Driver Profile Picture",
        modifier = modifier
    ) {
        val state = painter.state.collectAsState()
        when (state.value) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }

            is AsyncImagePainter.State.Error -> {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Driver Profile Picture",
                    modifier = Modifier
                        .height(240.dp)
                        .width(120.dp)

                )
            }

            else -> {
                SubcomposeAsyncImageContent(
                    modifier = modifier
                )
            }
        }
    }
}