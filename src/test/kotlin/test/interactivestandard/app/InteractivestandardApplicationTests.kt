package test.interactivestandard.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import test.interactivestandard.app.client.ImageFetchClient
import test.interactivestandard.app.entity.FileEntity
import test.interactivestandard.app.repository.FileRepository
import test.interactivestandard.app.utils.FileUtils.hash

@SpringBootTest
@ActiveProfiles("test")
class InteractivestandardApplicationTests : PostgresContainerWithMigrations() {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var imageFetchClient: ImageFetchClient

    @Autowired
    lateinit var fileRepository: FileRepository

    @BeforeEach
    fun cleanup() {
        runBlocking {
            withContext(Dispatchers.IO) {
                jdbcTemplate.execute("truncate table files")
            }
        }
    }

    @Test
    fun imageGetsDownloaded() {
        runBlocking {
            val output = imageFetchClient.getImageWithFilename(100, 100)

            Assertions.assertTrue(output.first.isNotEmpty())
            Assertions.assertTrue(output.second.isNotEmpty())
        }
    }

    @Test
    fun savingImageIntoDatabaseWorksForValidImage() {
        runBlocking {
            val output = imageFetchClient.getImageWithFilename(100, 100)

            val entity = FileEntity(
                output.second.hash(),
                output.first,
                output.second.size,
                IMAGE_JPEG_VALUE,
                output.second
            )
            assertDoesNotThrow {
                withContext(Dispatchers.IO) {
                    fileRepository.save(entity)
                }
            }

            runBlocking {
                withContext(Dispatchers.IO) {
                    fileRepository.count() == 1L
                }
            }

            runBlocking {
                withContext(Dispatchers.IO) {
                    jdbcTemplate.queryForObject("select 1 from summary", Int::class.java) == 1
                }
            }
        }
    }

    @Test
    fun savingImageIntoDatabaseDoesntWorkForDuplicateFile() {
        runBlocking {
            val output = imageFetchClient.getImageWithFilename(100, 100)

            val entity = FileEntity(
                output.second.hash(),
                output.first,
                output.second.size,
                IMAGE_JPEG_VALUE,
                output.second
            )

            assertDoesNotThrow {
                withContext(Dispatchers.IO) {
                    fileRepository.save(entity)
                    fileRepository.save(entity)
                }
            }

            runBlocking {
                withContext(Dispatchers.IO) {
                    fileRepository.count() == 1L
                }
            }

            runBlocking {
                withContext(Dispatchers.IO) {
                    jdbcTemplate.queryForObject("select 1 from summary", Int::class.java) == 1
                }
            }
        }
    }
}
