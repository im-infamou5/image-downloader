package test.interactivestandard.app.service

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class FilesSizeUpdateService(
    private val jdbcTemplate: JdbcTemplate
) {
    @PostConstruct
    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.SECONDS)
    private fun refreshSummary() {
        runBlocking(Dispatchers.IO) {
            jdbcTemplate.execute("refresh materialized view summary")
        }
    }
}
