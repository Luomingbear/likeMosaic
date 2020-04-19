package cn.bearever.likemosaic.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.SurfaceHolder;

import cn.bearever.likemosaic.call.LikeManager;
import io.agora.rtc.mediaio.AgoraSurfaceView;
import io.agora.rtc.mediaio.MediaIO;

/**
 * @author luoming
 * @date 2020/4/12
 */
public class MosaicVideoSink extends AgoraSurfaceView {
    private static final String TAG = "MosaicVideoSink";
    private boolean isLocalVideo = false;

    static {
        System.loadLibrary("mosaic");
    }

    /**
     * 构造函数
     *
     * @param context
     * @param isLocalVideo 是否是本地视频
     */
    public MosaicVideoSink(Context context, boolean isLocalVideo) {
        super(context);
        setPixelFormat(MediaIO.PixelFormat.I420);
        setBufferType(MediaIO.BufferType.BYTE_ARRAY);
        this.isLocalVideo = isLocalVideo;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
        Log.i(TAG, "surfaceChanged: w:" + width + ",h:" + height);
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int pixelFormat, int width, int height, int rotation, long ts) {
        int mosaiclevel = isLocalVideo ? LikeManager.getInstance().getLocalMosaicLevel() : LikeManager.getInstance().getRemoteMosaicLevel();
        if (mosaiclevel == 0) {
            super.consumeByteArrayFrame(data, pixelFormat, width, height, rotation, ts);
            return;
        }
        int bit = 64;
        byte[] out = mosaicI420(data, width, height, mosaiclevel, bit);
        super.consumeByteArrayFrame(out, pixelFormat, width, height, rotation, ts);
    }

    /**
     * 将视频帧转化为马赛克模式
     *
     * @param data
     * @param width
     * @param height
     * @param scale
     * @param bit    y分量的层级，默认是64，也就是把原始的256个值分成多少份
     * @return
     */
    public static native byte[] mosaicI420(byte[] data, int width, int height, int scale, int bit);
}
