package test.interactivestandard.app

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.io.FileWriter
import java.sql.DriverManager
import kotlin.random.Random

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
open class PostgresContainerWithMigrations : PostgreSQLContainer<Nothing>(
    DockerImageName.parse("postgres:latest")
) {
    companion object {
        const val changelogFilename = "db/changelog/db.changelog-master.yaml"

        @Container
        @ServiceConnection
        val container = PostgresContainerWithMigrations().apply {
            withDatabaseName("testdb-${Random.nextInt()}")
            withUsername("user-${Random.nextInt()}")
            withPassword("password")
            withDatabaseName("images")
        }
        init {
            container.start()
        }
    }

    override fun start() {
        super.start()
        System.getenv()["PG_STARTUP_SECONDS"]?.toIntOrNull()?.also { Thread.sleep(it * 1000L) }
        applyMigrations()
    }

    private fun PostgreSQLContainer<Nothing>.applyMigrations() {
        val connection = DriverManager.getConnection(jdbcUrl, username, password)
        val dataBase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
        val liquibase = Liquibase(changelogFilename, ClassLoaderResourceAccessor(), dataBase)
        liquibase.update(Contexts())
    }
}
