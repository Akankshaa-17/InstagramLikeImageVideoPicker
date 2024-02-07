package com.app.instagramlikeimagevideopicker.gallery

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * ImageUtil class.
 *
 * This class is used to render images across the app.
 */
object ImageUtils {

    // Checks if a volume containing external storage is available
// for read and write.
    // Returns true if external storage for photos is available
    private val isExternalStorageAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return state == Environment.MEDIA_MOUNTED
        }

    /**
     * Function to get rotated bitmap
     * @param bitmap input bitmap
     * @param rotate rotate angle
     * @return rotated bitmap
     */
    private fun getRotatedBitmap(bitmap: Bitmap, rotate: Int): Bitmap {
        if (rotate == 0) {
            return bitmap
        } else {
            val mat = Matrix()
            mat.postRotate(rotate.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
        }
    }

    /**
     * This method is used get orientation of camera photo
     *
     * @param context
     * @param imageUri  This parameter is Uri type
     * @param imagePath This parameter is String type
     * @return rotate
     */
    private fun getCameraPhotoOrientation(context: Context, imageUri: Uri?, imagePath: String): Int {
        var rotate = 0
        try {
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_NORMAL -> rotate = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    /**
     * Function to set selected image
     * @param orignalBitmap original bitmap
     * @param context instance of [Context]
     * @param imagePath image path
     * @param IMAGE_CAPTURE_URI image capture uri
     * @return instance of [Bitmap]
     */
    private fun setSelectedImage(orignalBitmap: Bitmap, context: Context, imagePath: String, IMAGE_CAPTURE_URI: Uri): Bitmap {
        try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (manufacturer.equals("samsung", ignoreCase = true) || model.equals("samsung", ignoreCase = true)) {
                rotateBitmap(context, orignalBitmap, imagePath, IMAGE_CAPTURE_URI)
            } else {
                orignalBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return orignalBitmap
        }

    }

    /**
     * Function to rotate bitmap
     * @param context instance of [Context]
     * @param bit input bitmap
     * @param imagePath image path
     * @param IMAGE_CAPTURE_URI image capture uri
     * @return instance of [Bitmap]
     */
    private fun rotateBitmap(context: Context, bit: Bitmap, imagePath: String, IMAGE_CAPTURE_URI: Uri): Bitmap {

        val rotation = getCameraPhotoOrientation(context, IMAGE_CAPTURE_URI, imagePath)
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bit, 0, 0, bit.width, bit.height, matrix, true)
    }

    /**
     * Utility function for decoding an image resource. The decoded bitmap will
     * be optimized for further scaling to the requested destination dimensions
     * and scaling logic.
     *
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Decoded bitmap
     */
    private fun decodeResource(filePath: String, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Bitmap {

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, bmOptions)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = calculateSampleSize(bmOptions.outWidth, bmOptions.outHeight, dstWidth, dstHeight, scalingLogic)

        return BitmapFactory.decodeFile(filePath, bmOptions)
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap
     * Bitmap to scale
     * @param dstWidth
     * Wanted width of destination bitmap
     * @param dstHeight
     * Wanted height of destination bitmap
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    private fun createScaledBitmap(unscaledBitmap: Bitmap, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Bitmap {
        val srcRect = calculateSrcRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val dstRect = calculateDstRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))

        return scaledBitmap
    }

    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    enum class ScalingLogic {
        CROP, FIT
    }

    /**
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
    private fun calculateSampleSize(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Int {
        if (scalingLogic == ScalingLogic.FIT) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                srcWidth / dstWidth
            } else {
                srcHeight / dstHeight
            }
        } else {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                srcHeight / dstHeight
            } else {
                srcWidth / dstWidth
            }
        }
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    private fun calculateSrcRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Rect {
        if (scalingLogic == ScalingLogic.CROP) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                val srcRectWidth = (srcHeight * dstAspect).toInt()
                val srcRectLeft = (srcWidth - srcRectWidth) / 2
                return Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight)
            } else {
                val srcRectHeight = (srcWidth / dstAspect).toInt()
                val scrRectTop = (srcHeight - srcRectHeight) / 2
                return Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight)
            }
        } else {
            return Rect(0, 0, srcWidth, srcHeight)
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    private fun calculateDstRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Rect {
        if (scalingLogic == ScalingLogic.FIT) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                Rect(0, 0, dstWidth, (dstWidth / srcAspect).toInt())
            } else {
                Rect(0, 0, (dstHeight * srcAspect).toInt(), dstHeight)
            }
        } else {
            return Rect(0, 0, dstWidth, dstHeight)
        }
    }


    /**
     * This method is used to resize image
     *
     * @param context
     * @param dstWidth
     * @param dstHeight
     * @param scalingLogic
     * @param currentPhotoPath
     * @return scaledBitmap
     */
    @JvmStatic
    fun getResizeProfileImage(context: Context, scalingLogic: ScalingLogic, currentPhotoPath: String, IMAGE_CAPTURE_URI: Uri?): Bitmap? {
        var rotate = 0
        try {
            val imageFile = File(currentPhotoPath)

            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            bmOptions.inJustDecodeBounds = false
            if (bmOptions.outWidth < 800) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
                return getRotatedBitmap(setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI!!), rotate)
            } else {
                val orientation: Int = getCameraPhotoOrientation(context, Uri.fromFile(File(currentPhotoPath)), currentPhotoPath)
                val dstHeight = 800
                val dstWidth = 800

                /*if (0 == orientation || 360 == orientation) {
                    //portrait image
                    dstWidth = 1080
                    dstHeight = (bmOptions.outHeight * dstWidth) / bmOptions.outWidth
                } else {
                    //landscape image
                    dstHeight = 1080
                    dstWidth = (bmOptions.outWidth * dstHeight) / bmOptions.outHeight
                }*/
                val unscaledBitmap = decodeResource(currentPhotoPath, dstWidth, dstHeight, scalingLogic)
                val scaledBitmap = createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic)
                unscaledBitmap.recycle()
                return getRotatedBitmap(scaledBitmap, rotate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getResizeLandscapeImage(context: Context, scalingLogic: ScalingLogic, currentPhotoPath: String, IMAGE_CAPTURE_URI: Uri?): Bitmap? {
        var rotate = 0
        try {
            val imageFile = File(currentPhotoPath)

            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


        try {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            bmOptions.inJustDecodeBounds = false
            if (bmOptions.outWidth < 1080) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
                return getRotatedBitmap(setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI!!), rotate)
            } else {
                val orientation: Int = getCameraPhotoOrientation(context, Uri.fromFile(File(currentPhotoPath)), currentPhotoPath)
                var dstHeight = 0
                var dstWidth = 0
                /*if (0 == orientation || 360 == orientation) {
                    //portrait image
                    dstWidth = 1080
                    dstHeight = (bmOptions.outHeight * dstWidth) / bmOptions.outWidth
                } else {
                    //landscape image
                    dstHeight = 1080
                    dstWidth = (bmOptions.outWidth * dstHeight) / bmOptions.outHeight
                }*/

                dstWidth = 1080
                dstHeight = (bmOptions.outHeight * dstWidth) / bmOptions.outWidth

                val unscaledBitmap = decodeResource(currentPhotoPath, dstWidth, dstHeight, scalingLogic)
                val scaledBitmap = createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic)
                unscaledBitmap.recycle()
                return getRotatedBitmap(scaledBitmap, rotate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getResizeImage(context: Context, currentPhotoPath: String, IMAGE_CAPTURE_URI: Uri?): Bitmap? {
        try {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            bmOptions.inJustDecodeBounds = false
            val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            return setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI!!)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    /**
     * Funttion to convert bitmap to file
     * @param bitmap input bitmap
     * @param dstPath destination path
     */
    @JvmStatic
    fun bitmapToFile(context: Context, bitmap: Bitmap, prefix: String? = "IMG_"): File? {
        try {
            return if (isExternalStorageAvailable) {
                val sdcard = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileNameString = "$prefix${timeStamp}.jpg"
                val pictureFile = File(sdcard, fileNameString)

                val fOut: FileOutputStream
                fOut = FileOutputStream(pictureFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
                fOut.flush()
                fOut.close()
                pictureFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getBitmapToFile(context: Context, bitmap: Bitmap): File? {
        try {
            return if (isExternalStorageAvailable) {

                val pictureFile = File.createTempFile("my_pic", ".jpeg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                if (pictureFile.exists()) {
                    pictureFile.delete()
                }

                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                val pic = bos.toByteArray()
                val fileOutputStream = FileOutputStream(pictureFile)
                fileOutputStream.write(pic)
                fileOutputStream.close()
                fileOutputStream.flush()

                pictureFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}