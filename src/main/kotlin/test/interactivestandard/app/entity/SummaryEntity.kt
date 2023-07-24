package test.interactivestandard.app.entity

import jakarta.persistence.*
import java.math.BigInteger

@Entity
@Table(name = "summary")
open class SummaryEntity {
    @Id
    @Column(name = "files_count", nullable = false)
    open val filesCount: BigInteger = BigInteger.ZERO
    @Column(name = "files_size")
    open val filesSize: BigInteger = BigInteger.ZERO
}