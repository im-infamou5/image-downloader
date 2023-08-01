package test.interactivestandard.app.service

import kotlinx.coroutines.*
import mu.KotlinLogging.logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import test.interactivestandard.app.client.ImageFetchClient
import test.interactivestandard.app.entity.FileEntity
import test.interactivestandard.app.repository.FileRepository
import test.interactivestandard.app.utils.FileSizeUtils
import test.interactivestandard.app.utils.FileUtils.hash
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.xml.bind.DatatypeConverter

@Service
@Profile("dev")
class ImageDispatchingService(
    private val imageFetchClient: ImageFetchClient,
    private val fileRepository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        private val TARGET_IMAGE_SIZES_INTERVAL = (10..5000)
        private val MAX_PARALLEL_RANGE_PER_100_CONNECTIONS = (0..7)
        private val availableCores = Runtime.getRuntime().availableProcessors() - 1
        private val log = logger {}
        private val pool = Executors.newFixedThreadPool(availableCores)
        const val URL_PREFIX = "https://loremflickr.com/cache/resized/"
    }

    suspend fun saveImageIntoDatabase() {

        val metadata = imageFetchClient.getImageWithFilename(
            TARGET_IMAGE_SIZES_INTERVAL.random(),
            TARGET_IMAGE_SIZES_INTERVAL.random()
        )
        val file = FileEntity(
            metadata.second.hash(),
            URL_PREFIX.plus(metadata.first),
            metadata.second.size,
            IMAGE_JPEG_VALUE,
            metadata.second
        )

        log.info { "Saving image with path: ${metadata.first} and content hash: ${metadata.second.hash()}" }

        runCatching {
            runBlocking(Dispatchers.IO) {
                if (fileRepository.existsById(file.id).not()) fileRepository.save(file)
            }
        }.onFailure {
            log.error { "Unexpected error happened on attempt of saving entity to database. Image path: ${metadata.first}, content hash: ${metadata.second.hash()}" }
            throw it
        }
        .onSuccess {
            log.info {
                "Saved image with path: ${metadata.first}, content hash: ${metadata.second.hash()} and size: ${
                    FileSizeUtils.humanReadableByteCountBin(metadata.second.size.toLong())
                }"
            }
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun fetchImages() {
        while (true) {
            runBlocking {
                coroutineScope {
                    MAX_PARALLEL_RANGE_PER_100_CONNECTIONS.map {
                        async(CoroutineScope(pool.asCoroutineDispatcher()).coroutineContext) {
                            saveImageIntoDatabase()
                        }
                    }
                }.awaitAll()
            }
        }
    }
}
