package test.interactivestandard.app

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.TestPropertySource
import test.interactivestandard.app.client.ImageFetchClient

@SpringBootTest
@Profile("test")
@TestPropertySource(properties = ["application-test.properties"])
class InteractivestandardApplicationTests {

    @Autowired
    lateinit var imageFetchClient: ImageFetchClient

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
    }

    @Test
    fun savingImageIntoDatabaseDoesntWorkForDuplicateFile() {
    }

}
