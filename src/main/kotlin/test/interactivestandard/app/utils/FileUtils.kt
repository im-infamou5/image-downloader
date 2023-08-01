package test.interactivestandard.app.utils

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object FileUtils {
    fun ByteArray.hash() =
        DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-256").digest(this)).lowercase()
}
