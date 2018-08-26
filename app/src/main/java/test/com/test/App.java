package test.com.test;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;

public class App extends Application {
    private static App app;
    private static int dpi;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    /**
     * Converts dp to pixel
     * @param dp value in dp
     * @return value in pixels
     */
    public static int getPx(int dp) {
        if (dpi == 0) {
            DisplayMetrics displayMetrics = app.getResources().getDisplayMetrics();
            dpi = displayMetrics.densityDpi;
        }
        return dp * dpi / 160;
    }

    /**
     * Exposes resources
     * @return resources
     */
    public static Resources getRes() {
        return app.getResources();
    }

    public static Context getContext() {
        return app.getApplicationContext();
    }

    /**
     * Creates a bitmap from vector drawables
     * @param context to use for the conversion
     * @param drawableId of the drawable to convert
     * @return created bitmap from the vector
     */
    public static Bitmap getBmpFromVctrDrwbl(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
