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

import io.agora.rtc.mediaio.AgoraSurfaceView;
import io.agora.rtc.mediaio.MediaIO;

/**
 * @author luoming
 * @date 2020/4/12
 */
public class MosaicVideoSink extends AgoraSurfaceView {
    private static final String TAG = "MosaicVideoSink";

    static {
        System.loadLibrary("mosaic");
    }

    public MosaicVideoSink(Context context) {
        super(context);
        setPixelFormat(MediaIO.PixelFormat.I420);
        setBufferType(MediaIO.BufferType.BYTE_ARRAY);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
//        mHolder = holder;
        Log.i(TAG, "surfaceChanged: w:" + width + ",h:" + height);
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int pixelFormat, int width, int height, int rotation, long ts) {
        int scale = 16;
        int bit = 32;
        byte[] out = mosaicI420(data, width, height, scale, bit);
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


    class FastYUVtoRGB {
        private RenderScript rs;
        private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
        private Type.Builder yuvType, rgbaType;
        private Allocation in, out;

        public FastYUVtoRGB(Context context) {
            rs = RenderScript.create(context);
            yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        }


        public Bitmap convertYUVtoRGB(byte[] yuvData, int width, int height) {
            if (yuvType == null) {
                yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvData.length);
                in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

                rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
                out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
            }
            in.copyFrom(yuvData);
            yuvToRgbIntrinsic.setInput(in);
            yuvToRgbIntrinsic.forEach(out);
            Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            out.copyTo(bmpout);
            return bmpout;
        }
    }

    class VideoFrameData {
        byte[] data;
        int width;
        int height;
    }
}
