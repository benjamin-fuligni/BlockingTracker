package com.davemorrissey.labs.subscaleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import io.github.benjamin_fuligni.blockingtracker.Pin;
import io.github.benjamin_fuligni.blockingtracker.R;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Selena on 10/31/2017.
 */

public class PinView extends SubsamplingScaleImageView {

    private HashMap hm;

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void newPin(String label, PointF location) {
        float density = getResources().getDisplayMetrics().densityDpi;
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.black_dot);
        float w = (density/6000f) * icon.getWidth();
        float h = (density/6000f) * icon.getHeight();
        icon = Bitmap.createScaledBitmap(icon, (int)w, (int)h, true);
        Pin pin = new Pin(label, location, icon);
        hm.put(pin.getLabel(), pin);
    }

    public boolean setPinLocation(String label, PointF location) {
        Pin pin = (Pin)hm.get(label);
        if (pin == null) {
            return false;
        }
        pin.setLocation(location);
        hm.put(pin.getLabel(), pin);
        return true;
    }

    public PointF getPinLocation(String label, PointF location) {
        Pin pin = (Pin)hm.get(label);
        if (pin == null) {
            return null;
        }
        return pin.getLocation();
    }

    private void initialise() {
        hm = new HashMap();
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

        Set set = hm.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if (me != null && (String)me.getKey() != null && (Pin)me.getValue() != null) {
                Log.d("***In PinView", (String)me.getKey());
                Pin pin = (Pin)me.getValue();
                PointF vPin = sourceToViewCoord(pin.getLocation());
                float vX = vPin.x - (pin.getIcon().getWidth()/2);
                float vY = vPin.y - pin.getIcon().getHeight();
                canvas.drawBitmap(pin.getIcon(), vX, vY, paint);
            }
        }
    }

}