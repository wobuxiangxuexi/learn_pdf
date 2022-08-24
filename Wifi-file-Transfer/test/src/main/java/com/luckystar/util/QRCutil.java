package com.luckystar.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRCutil {
    //生成二维码的方法
    public static Bitmap createShow(String content, int width, int height, String charset, String fault, String margin, int color_black, int color_white) {
//        内容为空停止程序
        if (TextUtils.isEmpty(content)) {
            return null;
        }
//        高度或宽度为空
        if (width < 0 || height < 0) {
            return null;
        }

        try {
            Hashtable<EncodeHintType, String> ht = new Hashtable<>();
//            字符转码
            if (!TextUtils.isEmpty(charset)) {
                ht.put(EncodeHintType.CHARACTER_SET, charset);
            }
//          容错率设置
            if (!TextUtils.isEmpty(fault)) {
                ht.put(EncodeHintType.ERROR_CORRECTION, fault);
            }
//            空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                ht.put(EncodeHintType.MARGIN, margin);
            }

//            矩阵对象
            BitMatrix encode = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, ht);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int i1 = 0; i1 < width; i1++) {
                    if (encode.get(i, i1)) {
                        pixels[i * width + i1] = color_black;
                    } else {
                        pixels[i * width + i1] = color_white;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

