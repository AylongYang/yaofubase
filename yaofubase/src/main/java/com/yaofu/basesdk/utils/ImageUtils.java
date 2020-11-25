package com.yaofu.basesdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class ImageUtils {
    public static String ImageUrl(String imageprex) {
        if (TextUtils.isEmpty(imageprex)) {
            return "";
        } else if (imageprex.startsWith("http")) {
            return imageprex;
        } else if (imageprex.startsWith("//")) {
            return "https:" + imageprex;
        } else {
            return "";
        }
    }

    // 通过view获取bitmap
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    // 将bitmap存储于本地
    public static void saveBitmapToSDCard(Bitmap photoBitmap, String path, String fileName) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File photoFile = new File(path, fileName + ".png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            photoFile.delete();
        } catch (IOException e) {
            photoFile.delete();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static final int defaultReqHeight = 240;
    public static final int defaultReqWidth = 400;


    /**
     * 计算缩放比例
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * @param @param  res
     * @param @param  resId
     * @param @param  reqWidth
     * @param @param  reqHeight
     * @param @return 设定文件
     * @return Bitmap 返回类型 @Title: decodeSampledBitmapFromResource @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 裁剪图片  参照固定尺寸
     */
    public static Bitmap cropBitmapCapture(Bitmap bitmap) {
        double width = 1224;
        double height = 634;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        double scaleWidth = width / bitmapWidth;
        double scaleHeight = height / bitmapHeight;
        int x = 0;
        int y = (int) (44 / scaleHeight);
        int w = (int) (960 / scaleWidth);
        int h = (int) (540 / scaleHeight);
        Bitmap capture = Bitmap.createBitmap(bitmap, x, y, w, h);
        bitmap.recycle();
        bitmap = null;
        return capture;
    }

    /**
     * 质量压缩
     */
    public static Bitmap compressImageToBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            int options = 100;
            while (outputStream.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
                outputStream.reset(); // 重置baos即清空baos
                options -= 10; // 每次都减少10
                if (options < 10) {
                    options = 10;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
                    break;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
            }

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return BitmapFactory.decodeStream(inputStream, null, null);
        }
        return null;
    }

    //保存文件到指定路径
    public static Bitmap saveImageToGallery(Context context, Bitmap bitmap) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "掌门优课";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (outputStream != null) {
                outputStream.flush();
            }
            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //保存图片后发送广播通知更新数据库
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        return bitmap;
    }

    /**
     * 文件路径
     */
    public static Bitmap bitmapCompressed(String srcPath) {
        return bitmapCompressed(srcPath, defaultReqWidth, defaultReqHeight);
    }

    /**
     * 采样率压缩
     */
    public static Bitmap bitmapCompressed(String srcPath, int reqWidth, int reqHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(srcPath, newOpts);
        if (newOpts.outHeight == -1 || newOpts.outWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(srcPath);
                int height = exifInterface.getAttributeInt(
                        ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL); //获取图片的高度
                int width = exifInterface.getAttributeInt(
                        ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL); //获取图片的宽度
                newOpts.outWidth = width;
                newOpts.outHeight = height;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight); // 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImageToBitmap(bitmap); // 压缩好比例大小后再进行质量压缩
    }

    /**
     * bitmap转base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream arrayOutputStream = null;
        try {
            if (bitmap != null) {
                arrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);

                arrayOutputStream.flush();
                arrayOutputStream.close();
                byte[] bitmapBytes = arrayOutputStream.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (arrayOutputStream != null) {
                    arrayOutputStream.flush();
                    arrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "data:image/png;base64," + result;
    }

    /**
     * 流
     */
    public static InputStream getImageStreamCompressed(String srcPath) {
        return getImageStreamCompressed(srcPath, defaultReqWidth, defaultReqHeight);
    }

    /**
     * 采样率压缩
     */
    public static InputStream getImageStreamCompressed(String srcPath, int reqWidth, int reqHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(srcPath, newOpts);
        if (newOpts.outHeight == -1 || newOpts.outWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(srcPath);
                int height = exifInterface.getAttributeInt(
                        ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL); //获取图片的高度
                int width = exifInterface.getAttributeInt(
                        ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL); //获取图片的宽度
                newOpts.outWidth = width;
                newOpts.outHeight = height;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight); // 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImageToStream(bitmap); // 压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩
     */
    public static InputStream compressImageToStream(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (bmp != null) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream); // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (outputStream.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
                outputStream.reset(); // 重置baos即清空baos
                options -= 10; // 每次都减少10
                if (options < 10) {
                    options = 10;
                    bmp.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
                    break;
                }
                bmp.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(outputStream.toByteArray()); // 把压缩后的数据baos存放到ByteArrayInputStream中
            bmp.recycle();
            return isBm;
        }
        return null;
    }

    public static Bitmap getImageBitmap(InputStream stream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(stream, null, options);
        //int imageHeight = options.outHeight;
        //int imageWidth = options.outWidth;
        //// recreate the stream
        //// make some calculation to define inSampleSize
        options.inSampleSize = calculateInSampleSize(options, defaultReqWidth, defaultReqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        if (bitmap != null) {
            return compressImageToBitmap(bitmap);
        }
        return bitmap;
    }


}
