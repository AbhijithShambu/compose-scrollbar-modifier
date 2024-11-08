import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.ScrollbarConfig
import com.shambu.compose.scrollbar.horizontalScrollWithScrollbar
import com.shambu.compose.scrollbar.rememberScrollbarState
import com.shambu.compose.scrollbar.sample.data.AlbumModel
import com.shambu.compose.scrollbar.sample.data.SongModel
import com.shambu.compose.scrollbar.sample.theme.AppTheme
import com.shambu.compose.scrollbar.sample.ui.components.AlbumCover
import com.shambu.compose.scrollbar.sample.ui.components.SongItem
import com.shambu.compose.scrollbar.verticalScrollWithScrollbar

@Composable
fun App() {
    AppTheme(false) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .verticalScrollWithScrollbar(
                    rememberScrollState(),
                    rememberScrollbarState(),
                    scrollbarConfig =
                        ScrollbarConfig(
                            padding = PaddingValues(end = 4.dp),
                        ),
                ).padding(vertical = 24.dp),
        ) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Melody",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h5,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .semantics { contentDescription = "title" },
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "What do you want to hear",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.subtitle1,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .semantics { contentDescription = "subtitle" },
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Popular Albums",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .semantics { contentDescription = "popular releases" },
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScrollWithScrollbar(
                        rememberScrollState(),
                        rememberScrollbarState(),
                        scrollbarConfig =
                            ScrollbarConfig(
                                indicatorColor = Color(0xffd62a47),
                                indicatorThickness = 16.dp,
                                barColor = Color(0x88d42aaa),
                                barThickness = 24.dp,
                                padding = PaddingValues(horizontal = 80.dp),
                                showAlways = true,
                            ),
                    ).padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                sampleAlbums.forEach { poster ->
                    AlbumCover(poster, width = 260.dp)
                }
            }
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Popular Songs",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .semantics { contentDescription = "popular releases" },
                )
                Spacer(Modifier.height(8.dp))
                sampleAlbums[0].songs.forEach { song ->
                    SongItem(song)
                }
            }
        }
    }
}

expect fun getPlatformName(): String

val songTitles =
    listOf(
        "Starlight Serenade",
        "Whispers in the Wind",
        "Electric Dreams",
        "Crimson Sky",
        "Lost in the Echoes",
        "Dancing in the Rain",
        "Emerald Isle",
        "Golden Hour",
        "Midnight Bloom",
        "Silent Symphony",
    )

val sampleAlbums =
    listOf(
        AlbumModel(
            title = "Cosmic Odyssey",
            description = "Great Titan",
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            style = "Sci-Fi",
            songs = (
                songTitles.mapIndexed { index, it ->
                    SongModel(
                        it,
                        "3:30",
                        "https://picsum.photos/id/${200 + index}/200/300",
                        "Cosmic Oddesey",
                    )
                }
            ),
        ),
        AlbumModel(
            title = "Soundwave Festival",
            description = "Flivorous Bunch",
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            style = "Indie Rock",
            songs = (1..10).map { SongModel("Track $it", "3:30", "", "Soundwave Festival") },
        ),
        AlbumModel(
            title = "Escape to Paradise",
            description = "Jack Wince",
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            style = "Tropical",
            songs = (1..10).map { SongModel("Track $it", "3:30", "", "Escape to Paradise") },
        ),
        AlbumModel(
            title = "Innovate 2024",
            description = "Shady Zack",
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            style = "Tech Conference",
            songs = (1..10).map { SongModel("Track $it", "3:30", "", "Innovate 2024") },
        ),
        AlbumModel(
            title = "Unleash Your Potential",
            description = "Flayer",
            imageUrl = "https://picsum.photos/seed/picsum/200/300",
            style = "Fitness",
            songs = (1..10).map { SongModel("Track $it", "3:30", "", "Unleash Your Potential") },
        ),
    ).mapIndexed { index, it -> it.copy(imageUrl = "https://picsum.photos/id/${250 + index}/200/300") }
