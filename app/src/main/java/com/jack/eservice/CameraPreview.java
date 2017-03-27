package com.jack.eservice;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by jason on 2017/3/14.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mcamera;
    private Camera.PreviewCallback previewCallback;
    private Camera.AutoFocusCallback autoFocusCallback;

    public CameraPreview(Context context,Camera camera,Camera.PreviewCallback previewCb,Camera.AutoFocusCallback autoFocusCb){
        super(context);
        mcamera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mcamera.setPreviewDisplay(holder);
        }catch (IOException e){
            Log.d("bug","error camera"+e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null){
            return;
        }
        try{
            mcamera.stopPreview();
        }catch (Exception e){

        }
        try{
            mcamera.setDisplayOrientation(90);
            mcamera.setPreviewDisplay(mHolder);
            mcamera.setPreviewCallback(previewCallback);
            mcamera.startPreview();
            mcamera.autoFocus(autoFocusCallback);
        }catch (Exception e){
            Log.d("debug","error start camera"+e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
