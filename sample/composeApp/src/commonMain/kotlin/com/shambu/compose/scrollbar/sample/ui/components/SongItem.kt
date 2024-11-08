package com.shambu.compose.scrollbar.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.sample.data.SongModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun SongItem(
    poster: SongModel,
    modifier: Modifier = Modifier,
    actionButton: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier
            .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KamelImage(
            resource = { asyncPainterResource(poster.imageUrl) },
            contentDescription = poster.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(68.dp)
                .clip(MaterialTheme.shapes.large)
                .background(Color(0xFFC6A9BF)),
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                poster.title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { contentDescription = "title" },
            )

            Spacer(Modifier.height(4.dp))

            Text(
                poster.description,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.semantics { contentDescription = "description" },
            )
        }

        Spacer(Modifier.width(8.dp))

        if (actionButton != null) {
            actionButton()
            Spacer(Modifier.width(8.dp))
        }
    }
}
