package com.mathbank.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfProcessor(private val context: Context) {

    companion object {
        private const val TAG = "PdfProcessor"
        // Sayfa DPI: 150 dpi - kalite ve dosya boyutu dengesi
        private const val RENDER_SCALE = 2.0f
    }

    data class PageResult(
        val pageIndex: Int,
        val bitmap: Bitmap,
        val width: Int,
        val height: Int
    )

    /**
     * PDF'deki toplam sayfa sayısını döner
     */
    suspend fun getPageCount(uri: Uri): Int = withContext(Dispatchers.IO) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext 0
            parcelFileDescriptor.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    renderer.pageCount
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getPageCount error", e)
            0
        }
    }

    /**
     * Belirli bir sayfayı Bitmap'e render eder
     */
    suspend fun renderPage(uri: Uri, pageIndex: Int): PageResult? = withContext(Dispatchers.IO) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext null

            parcelFileDescriptor.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    if (pageIndex >= renderer.pageCount) return@withContext null

                    renderer.openPage(pageIndex).use { page ->
                        val width = (page.width * RENDER_SCALE).toInt()
                        val height = (page.height * RENDER_SCALE).toInt()

                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        // Beyaz arka plan
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        PageResult(
                            pageIndex = pageIndex,
                            bitmap = bitmap,
                            width = width,
                            height = height
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "renderPage error for page $pageIndex", e)
            null
        }
    }

    /**
     * Tüm sayfaları sırayla render eder, her sayfa için callback çağırır
     */
    suspend fun renderAllPages(
        uri: Uri,
        onPageRendered: suspend (PageResult) -> Unit,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ) = withContext(Dispatchers.IO) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext

            parcelFileDescriptor.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    val totalPages = renderer.pageCount
                    Log.d(TAG, "Rendering $totalPages pages")

                    for (i in 0 until totalPages) {
                        onProgress(i, totalPages)

                        renderer.openPage(i).use { page ->
                            val width = (page.width * RENDER_SCALE).toInt()
                            val height = (page.height * RENDER_SCALE).toInt()

                            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            bitmap.eraseColor(Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                            onPageRendered(PageResult(i, bitmap, width, height))
                        }
                    }
                    onProgress(totalPages, totalPages)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "renderAllPages error", e)
            throw e
        }
    }

    /**
     * URI'dan dosya adını çıkarır
     */
    fun getFileName(uri: Uri): String {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: uri.lastPathSegment ?: "unknown.pdf"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "unknown.pdf"
        }
    }
}
