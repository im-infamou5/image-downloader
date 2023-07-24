package test.interactivestandard.app.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types


@Entity
@Table(name = "files")
data class FileEntity(
    @Id @Column(name = "id", nullable = false) val id: String,
    @Column(name = "url", nullable = false) val url: String,
    @Column(name = "size", nullable = false) val size: Int,
    @Column(name = "content_type", nullable = false) val contentType: String,
    @JdbcTypeCode(Types.VARBINARY) @Column(name = "content", nullable = false) val content: ByteArray,
)