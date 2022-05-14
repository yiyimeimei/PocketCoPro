/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.idcardquality;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.baidu.idl.authority.AlgorithmOnMainThreadException;
import com.baidu.idl.authority.IDLAuthorityException;
import com.baidu.idl.license.License;
import com.baidu.idl.util.UIThread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IDCardQualityProcess {
    final ReentrantReadWriteLock nativeModelLock = new ReentrantReadWriteLock();
    private static IDCardQualityProcess mInstance;
    private static int mAuthorityStatus;
    private static Throwable loadNativeException = null;
    private static volatile boolean hasReleased;

    public IDCardQualityProcess() {
    }

    public static synchronized IDCardQualityProcess getInstance() {
        if (null == mInstance) {
            mInstance = new IDCardQualityProcess();
        }

        return mInstance;
    }

    public native byte[] convertRGBImage(int[] colors, int width, int height);

    public native int idCardQualityModelInit(AssetManager var1, String var2);

    public native int idCardQualityCaptchaRelease();

    public native int idCardQualityProcess(byte[] var1, int var2, int var3, boolean var4, int var5);

    public static synchronized int init(String token) throws AlgorithmOnMainThreadException, IDLAuthorityException {
        if (UIThread.isUITread()) {
            throw new AlgorithmOnMainThreadException();
        } else {

            try {
                mAuthorityStatus = License.getInstance().init(token);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            return mAuthorityStatus;
        }
    }

    public int idCardQualityInit(AssetManager assetManager, String modelPath) {
        if (mAuthorityStatus == 0) {
            hasReleased = false;
            nativeModelLock.writeLock().lock();
            int status = this.idCardQualityModelInit(assetManager, modelPath);
            nativeModelLock.writeLock().unlock();
            return status;
        } else {
            return mAuthorityStatus;
        }
    }

    public int idCardQualityRelease() {
        if (mAuthorityStatus == 0) {
            hasReleased = true;
            nativeModelLock.writeLock().lock();
            this.idCardQualityCaptchaRelease();
            nativeModelLock.writeLock().unlock();
            return 0;
        } else {
            return mAuthorityStatus;
        }
    }

    public int idCardQualityDetectionImg(Bitmap img, boolean isFont) {
        int status;
        if (mAuthorityStatus == 0) {
            if (hasReleased) {
                return -1;
            }
            int imgHeight = img.getHeight();
            int imgWidth = img.getWidth();
            byte[] imageData = this.getRGBImageData(img);
            nativeModelLock.readLock().lock();
            status = this.idCardQualityProcess(imageData, imgHeight, imgWidth, isFont, 3);
            nativeModelLock.readLock().unlock();
            return status;
        } else {
            return mAuthorityStatus;
        }
    }

    public static Throwable getLoadSoException() {
        return loadNativeException;
    }

    public byte[] getRGBImageData(Bitmap img) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        int[] pixels = new int[imgWidth * imgHeight];
        img.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
        return convertRGBImage(pixels, imgWidth, imgHeight);
    }

    public void releaseModel() {
        this.idCardQualityRelease();
    }

    static {
        try {
            System.loadLibrary("idl_license");
            System.loadLibrary("idcard_quality.1.1.1");
        } catch (Throwable e) {
            loadNativeException = e;
        }
        mInstance = null;
        mAuthorityStatus = 256;
    }
}
