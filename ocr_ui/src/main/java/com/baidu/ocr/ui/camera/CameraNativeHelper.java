/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.ocr.ui.camera;

import android.content.Context;

import com.baidu.idcardquality.IDCardQualityProcess;

/**
 * Created by ruanshimin on 2018/1/23.
 */

public class CameraNativeHelper {

    public interface CameraNativeInitCallback {
        /**
         * 加载本地库异常回调
         *
         * @param errorCode 错误代码
         * @param e         如果加载so异常则会有异常对象传入
         */
        void onError(int errorCode, Throwable e);
    }

    public static void init(final Context ctx, final String token, final CameraNativeInitCallback cb) {
        CameraThreadPool.execute(() -> {
            int status;
            // 加载本地so失败, 异常返回getLoadSoException
            if (IDCardQualityProcess.getLoadSoException() != null) {
                status = CameraView.NATIVE_SO_LOAD_FAIL;
                cb.onError(status, IDCardQualityProcess.getLoadSoException());
                return;
            }
            // 授权状态
            int authStatus = IDCardQualityProcess.init(token);
            if (authStatus != 0) {
                cb.onError(CameraView.NATIVE_AUTH_FAIL, null);
                return;
            }

            // 加载模型状态
            int initModelStatus = IDCardQualityProcess.getInstance()
                    .idCardQualityInit(ctx.getAssets(),
                            "models");

            if (initModelStatus != 0) {
                cb.onError(CameraView.NATIVE_INIT_FAIL, null);
            }
        });
    }

    public static void release() {
        IDCardQualityProcess.getInstance().releaseModel();
    }
}
