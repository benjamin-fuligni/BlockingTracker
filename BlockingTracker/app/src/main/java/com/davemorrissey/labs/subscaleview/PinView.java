package com.davemorrissey.labs.subscaleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import io.github.benjamin_fuligni.blockingtracker.Pin;
import io.github.benjamin_fuligni.blockingtracker.R;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Selena on 10/31/2017.
 */

public class PinView extends SubsamplingScaleImageView {

    private Context context;
    private HashMap hm;
    private int pinId = -1;
    private Pin currentPin = null;
    private Paint paint;
    private Paint selectedPaint;

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();
    }

    private void initialise() {
        hm = new HashMap();
        paint = new Paint();
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        selectedPaint = new Paint(paint);
        selectedPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
    }

    public void newPin(String label, PointF location) {
        float density = getResources().getDisplayMetrics().densityDpi;
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.black_dot);
        float w = (density / 10000f) * icon.getWidth();
        float h = (density / 10000f) * icon.getHeight();
        icon = Bitmap.createScaledBitmap(icon, (int) w, (int) h, true);
        Pin pin = new Pin(hm.size(), label, location, icon);
        hm.put(pin.getPinId(), pin);
        invalidate();
    }

    /*
    public boolean setPinLocation(int id, PointF location) {
        Pin pin = (Pin)hm.get(id);
        if (pin == null) {
            return false;
        }
        pin.setLocation(location);
        hm.put(id, pin);
        return true;
    }

    public HashMap getHm() {
        return hm;
    }

    public PointF getPinLocation(int id, PointF location) {
        Pin pin = (Pin)hm.get(id);
        if (pin == null) { return null; }
        return pin.getLocation();
    }
    */

    public List<Pin> getPins () {
        List<Pin> pins = new ArrayList<>();
        Set set = hm.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if (me != null && (int) me.getKey() > -1 && me.getValue() != null) {
                Pin pin = (Pin)me.getValue();
                pins.add(pin);
            }
        }
        return pins;
    }

    public boolean deleteCurrentPin() {
        if (pinId > -1 && currentPin != null) {
            hm.remove(currentPin.getPinId());
            pinId = -1;
            currentPin = null;
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int eventaction = event.getAction();

        float X = event.getX();
        float Y = event.getY();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a pin
                // pin is currently selected
                if (pinId > -1 && currentPin != null) {
                    Y = Y + currentPin.getIcon().getHeight() / 2;
                    PointF sPin = viewToSourceCoord(new PointF(X, Y));
                    currentPin.setLocation(sPin);
                    hm.put(pinId, currentPin);
                    currentPin.setSelected(false);
                    pinId = -1;
                    currentPin = null;
                } else {
                    Set set = hm.entrySet();
                    Iterator i = set.iterator();

                    while (i.hasNext()) {
                        Map.Entry me = (Map.Entry) i.next();
                        if (me != null && (int) me.getKey() > -1 && (Pin) me.getValue() != null) {
                            Pin pin = (Pin) me.getValue();
                            PointF vPin = sourceToViewCoord(pin.getLocation());
                            float pinXmin = vPin.x - (pin.getIcon().getWidth() / 2);
                            float pinXmax = vPin.x + (pin.getIcon().getWidth() / 2);
                            float pinYmin = vPin.y - pin.getIcon().getHeight();
                            float pinYmax = vPin.y;
                            // check all the bounds of the pin (square)
                            if (X > pinXmin && X < pinXmax && Y > pinYmin && Y < pinYmax) {
                                pinId = pin.getPinId();
                                currentPin = pin;
                                pin.setSelected(true);
                                break;
                            }
                        }
                    }
                }
                break;
        }
        // redraw the canvas
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) { return; }

        Set set = hm.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if (me != null && (int) me.getKey() > -1 && (Pin)me.getValue() != null) {
                Pin pin = (Pin)me.getValue();
                PointF vPin = sourceToViewCoord(pin.getLocation());
                float vX = vPin.x - (pin.getIcon().getWidth() / 2);
                float vY = vPin.y - (pin.getIcon().getHeight() / 2);
                if (pin.isSelected()) {
                    canvas.drawCircle(vPin.x, vY, pin.getIcon().getWidth()/2, selectedPaint);
                } else {
                    canvas.drawCircle(vPin.x, vY, pin.getIcon().getWidth()/2, paint);
                }
                canvas.drawText(pin.getLabel(), vX, (vY - pin.getIcon().getHeight()), paint);
            }
        }
    }
}