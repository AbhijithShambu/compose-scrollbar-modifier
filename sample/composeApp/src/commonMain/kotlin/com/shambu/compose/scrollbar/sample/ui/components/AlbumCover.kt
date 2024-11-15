package com.shambu.compose.scrollbar.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.sample.data.AlbumModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun AlbumCover(
    album: AlbumModel,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val aspectRatio = 1.15f

    Box(
        modifier
            .width(width)
            .aspectRatio(aspectRatio)
            .clip(MaterialTheme.shapes.large)
            .background(Color.LightGray.copy(alpha = 0.5f)),
    ) {
        // Background image
        KamelImage(
            resource = { asyncPainterResource(album.imageUrl) },
            contentDescription = "Album art",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )

        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .shadow(4.dp)
                .background(MaterialTheme.colors.surface)
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    album.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { contentDescription = "album_title" },
                    color = MaterialTheme.colors.onSurface,
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    album.description,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.semantics { contentDescription = "album_artist" },
                    color = MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}
