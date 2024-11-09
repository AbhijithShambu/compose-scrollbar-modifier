import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.ColorType
import com.shambu.compose.scrollbar.ScrollbarConfig
import com.shambu.compose.scrollbar.ScrollbarState
import com.shambu.compose.scrollbar.horizontalScrollWithScrollbar
import com.shambu.compose.scrollbar.rememberScrollbarState
import com.shambu.compose.scrollbar.sample.data.AlbumModel
import com.shambu.compose.scrollbar.sample.data.SongModel
import com.shambu.compose.scrollbar.sample.theme.AppTheme
import com.shambu.compose.scrollbar.sample.ui.components.AlbumCover
import com.shambu.compose.scrollbar.sample.ui.components.SongItem
import com.shambu.compose.scrollbar.verticalScrollWithScrollbar

const val DARK_THEME = false
val indicatorColor = Color(0xffB33951)
val barColor = Color(0xFFEEDDEE)

@Composable
fun App() {
    AppTheme(DARK_THEME) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .verticalScrollWithScrollbar(
                    rememberScrollState(),
                    rememberScrollbarState(),
                    scrollbarConfig =
                        ScrollbarConfig(
                            padding = PaddingValues(end = 5.dp, top = 8.dp),
                            indicatorColor = ColorType.Solid(indicatorColor),
                            indicatorThickness = 16.dp,
                            barThickness = 2.dp,
                            indicatorPadding = PaddingValues(2.dp),
                            barColor = ColorType.Solid(barColor),
                        ),
                ).padding(vertical = 24.dp),
        ) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Melody",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
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

                Spacer(Modifier.height(24.dp))

                Text(
                    "Popular Albums",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
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
                    .background(Color(if (DARK_THEME) 0xFF0C051B else 0xFF7B506F))
                    .padding(vertical = 16.dp)
                    .customScrollbar(rememberScrollState(), rememberScrollbarState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                sampleAlbums.forEach { poster ->
                    AlbumCover(
                        poster,
                        width = 260.dp,
                        modifier = Modifier.shadow(6.dp, RoundedCornerShape(20.dp)),
                    )
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
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .semantics { contentDescription = "popular releases" },
                )

                sampleAlbums[0].songs.forEach { song ->
                    SongItem(song, modifier = Modifier.shadow(1.dp, MaterialTheme.shapes.large))
                }
            }
        }
    }
}

fun Modifier.customScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
): Modifier =
    horizontalScrollWithScrollbar(
        scrollState,
        scrollbarState,
        scrollbarConfig =
            ScrollbarConfig(
                indicatorThickness = 24.dp,
                barThickness = 24.dp,
                padding = PaddingValues(horizontal = 80.dp),
                indicatorPadding = PaddingValues(4.dp),
                indicatorColor = ColorType.Gradient { indicatorBounds ->
                    Brush.linearGradient(
                        0f to indicatorColor,
                        0.55f to Color(0xFFFFFF),
                        1f to indicatorColor,
                        start = indicatorBounds.topLeft,
                        end = indicatorBounds.bottomRight,
                    )
                },
                barColor = ColorType.Solid(barColor),
                showAlways = true,
            ),
    )

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
                        "https://picsum.photos/id/${28 + index * 10}/200/300",
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
    ).mapIndexed { index, it -> it.copy(imageUrl = "https://picsum.photos/id/${25 + index}/200/300") }
