package ir.mahdiparastesh.instatools

import java.io.FileInputStream

fun main() {
    println(FileInputStream("cookies.txt").use { String(it.readBytes()) })
}
