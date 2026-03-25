package dev.alexmester.impl.presentation.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedArticleCard
import dev.alexmester.ui.desing_system.LaskTheme

class NewsFeedArticlePreviewProvider : PreviewParameterProvider<NewsArticle> {
    override val values = sequenceOf(
        // С картинкой, позитивная тональность, один автор
        NewsArticle(
            id = 1L,
            title = "Biden Signs Major Climate Bill Into Law After Senate Approval",
            text = null,
            summary = null,
            url = "https://example.com/article/1",
            image = "https://politicalwire.com/wp-content/uploads/2018/02/PW-podcast-logo.jpg",
            video = null,
            publishDate = "2026-03-22 10:30:00",
            authors = listOf("John Smith"),
            category = "politics",
            language = "en",
            sourceCountry = "us",
            sentiment = 0.5,
        ),
        // Без картинки, негативная тональность, несколько авторов
        NewsArticle(
            id = 2L,
            title = "Markets Crash as Global Economic Uncertainty Reaches New High",
            text = null,
            summary = null,
            url = "https://example.com/article/2",
            image = null,
            video = null,
            publishDate = "2026-03-22 09:15:00",
            authors = listOf("Jane Doe", "Mike Johnson"),
            category = "business",
            language = "en",
            sourceCountry = "us",
            sentiment = -0.7,
        ),
        // С картинкой, нейтральная тональность, без автора
        NewsArticle(
            id = 3L,
            title = "Scientists Discover New Species in Amazon Rainforest",
            text = null,
            summary = null,
            url = "https://example.com/article/3",
            image = "https://politicalwire.com/wp-content/uploads/2018/02/PW-podcast-logo.jpg",
            video = null,
            publishDate = "2026-03-21 14:00:00",
            authors = emptyList(),
            category = "science",
            language = "en",
            sourceCountry = "us",
            sentiment = 0.0,
        ),
        // Очень длинный заголовок
        NewsArticle(
            id = 4L,
            title = "International Summit on Artificial Intelligence Regulation Concludes With Historic Agreement Between 50 Nations on Safety Standards",
            text = null,
            summary = null,
            url = "https://example.com/article/4",
            image = "https://politicalwire.com/wp-content/uploads/2018/02/PW-podcast-logo.jpg",
            video = null,
            publishDate = "2026-03-20 18:45:00",
            authors = listOf("Sarah Connor"),
            category = "technology",
            language = "en",
            sourceCountry = "us",
            sentiment = 0.3,
        ),
    )
}


// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Article Card — Light", showBackground = true)
@Composable
private fun NewsFeedArticleCardPreview(
    @PreviewParameter(NewsFeedArticlePreviewProvider::class) article: NewsArticle,
) {
    LaskTheme(darkTheme = false) {
        NewsFeedArticleCard(
            article = article,
            onClick = {},
            modifier = Modifier,
        )
    }
}

@Preview(name = "Article Card — Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun NewsFeedArticleCardDarkPreview(
    @PreviewParameter(NewsFeedArticlePreviewProvider::class) article: NewsArticle,
) {
    LaskTheme(darkTheme = true) {
        NewsFeedArticleCard(
            article = article,
            onClick = {},
            modifier = Modifier,
        )
    }
}

@Preview(name = "Article Cards — Feed", showBackground = true)
@Composable
private fun NewsFeedArticleCardListPreview() {
    LaskTheme(darkTheme = false) {
        Column {
            NewsFeedArticlePreviewProvider().values.forEach { article ->
                NewsFeedArticleCard(
                    article = article,
                    onClick = {},
                    modifier = Modifier,
                )
            }
        }
    }
}