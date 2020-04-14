package cn.bearever.mingbase.app.util;

import android.content.Context;
import android.widget.Toast;

/**
 * toast 工具类
 * @author luoming
 * @date 2020/4/12
 */
public class ToastUtil {

    /**
     * 显示toast
     * @param context
     * @param text
     */
    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
