package cn.bearever.likemosaic.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;

import io.agora.rtc.mediaio.AgoraSurfaceView;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;

/**
 * @author luoming
 * @date 2020/4/12
 */
public class MosaicVideoSink extends AgoraSurfaceView {
    private static final String TAG = "MosaicVideoSink";
    private SurfaceHolder mHolder;
    private FastYUVtoRGB mFastYUV2RGB;
    private Paint mPaint;

    public MosaicVideoSink(Context context) {
        super(context);
        setPixelFormat(MediaIO.PixelFormat.NV21);
        setBufferType(MediaIO.BufferType.BYTE_ARRAY);
        mFastYUV2RGB = new FastYUVtoRGB(context);
        mPaint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
//        mHolder = holder;
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int pixelFormat, int width, int height, int rotation, long ts) {
//        super.consumeByteArrayFrame(data, pixelFormat, width, height, rotation, ts);
        // TODO: 2020/4/12 自定义渲染
        for (int i = 0; i < 16; i++) {
            Log.i(TAG, "consumeByteArrayFrame: " + i + ":" + data[i]);
        }
        Log.i(TAG, "consumeByteArrayFrame: ");
        Canvas canvas = mHolder.lockCanvas(null);
        if (canvas != null) {
            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dest = new Rect(0, 0, width, height);
            canvas.drawBitmap(mFastYUV2RGB.convertYUVtoRGB(data, width, height), src, dest, mPaint);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }


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
}
