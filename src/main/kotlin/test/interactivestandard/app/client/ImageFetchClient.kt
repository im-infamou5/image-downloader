package test.interactivestandard.app.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class ImageFetchClient(
    private val webClient: WebClient,
) {
    companion object {
        const val PATH_PREFIX = "/cache/resized/"
        private val log = KotlinLogging.logger {}
    }

    suspend fun getImageWithFilename(high: Int, width: Int): Pair<String, ByteArray> {
        return kotlin.runCatching {
            coroutineScope {
                webClient.get()
                    .uri("https://loremflickr.com/$high/$width").retrieve().toBodilessEntity().flatMap {
                        it.headers["Location"]?.get(0)?.toMono() ?: Mono.empty()
                    }.map { redirectUrl ->
                        val image = fetchImageContent(redirectUrl)
                        redirectUrl.removePrefix(PATH_PREFIX) to image
                    }.awaitSingle()
            }
        }
        .onFailure {
            log.error { "Error occured while downloading image: ${it.cause}" }
            throw it
        }
        .onSuccess {
            log.info { "Successfully downloaded image with name : ${it.first}" }
        }.getOrThrow()
    }

    private fun fetchImageContent(redirectUrl: String): ByteArray {
        return runBlocking(Dispatchers.IO) {
            webClient.get()
                .uri("https://loremflickr.com$redirectUrl").retrieve().awaitBody()
        }
    }
}

