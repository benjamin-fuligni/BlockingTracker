package com.davemorrissey.labs.subscaleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import io.github.benjamin_fuligni.blockingtracker.R;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

/**
 * Created by Selena on 10/31/2017.
 */

public class PinView extends SubsamplingScaleImageView {

    private ArrayList Pins = new ArrayList();
    //private PointF sPin;
    private Bitmap pin;

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void setPin(PointF sPin) {
        //this.sPin = sPin;
        Pins.add(sPin);
        initialise();
        invalidate();
    }

    /*
    public PointF getPin() {
        return sPin;
    }
    */

    public ArrayList getPin() {
        return Pins;
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.black_dot);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //if (sPin != null && pin != null) {
        if (Pins.size() != 0 && pin != null) {
            for (int i = 0; i < Pins.size(); i++) {
                //PointF vPin = sourceToViewCoord(sPin);
                PointF vPin = sourceToViewCoord((PointF)Pins.get(i));
                float vX = vPin.x - (pin.getWidth() / 2);
                float vY = vPin.y - pin.getHeight();
                canvas.drawBitmap(pin, vX, vY, paint);
            }
        }

    }

}