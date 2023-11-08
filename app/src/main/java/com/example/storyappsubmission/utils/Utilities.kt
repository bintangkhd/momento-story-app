package com.example.storyappsubmission.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Patterns
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PASSWORD_MIN_LENGTH = 8
private const val NAME_MAX_LENGTH = 20
private const val UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
private const val MAX_STREAM_LENGTH = 1000000
private const val BYTE_ARRAY = 1024
private const val IMAGE_FORMAT = ".jpg"
private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun validateEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): Boolean {
    return !TextUtils.isEmpty(password) && password.length >= PASSWORD_MIN_LENGTH
}

fun maxName(name: String): Boolean {
    return !TextUtils.isEmpty(name) && name.length <= NAME_MAX_LENGTH
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat(UTC_DATE_FORMAT, Locale.UK)
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
}

fun reduceImageSize(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAX_STREAM_LENGTH)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = tempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(BYTE_ARRAY)
    var len: Int

    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun tempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, IMAGE_FORMAT, storageDir)
}
