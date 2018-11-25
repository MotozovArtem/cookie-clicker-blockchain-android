package ru.rienel.cookie_clicker_blockchain_android.view;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import ru.rienel.cookie_clicker_blockchain_android.R;

public class DonutView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmapResource = BitmapFactory.decodeResource(getResources(), R.drawable.donut_chocolate);
    private Bitmap bitmap;

    private void init() {
        Matrix matrix = new Matrix();
        matrix.postScale(10,10);
        bitmap = Bitmap.createBitmap(bitmapResource, 0, 0,
                bitmapResource.getWidth()/2, bitmapResource.getHeight()/2, matrix, true);
    }

    public DonutView(Context context) {
        super(context);
        init();

    }

    public DonutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public DonutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);

    }
}
