package io.github.benjamin_fuligni.blockingtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Selena on 10/31/2017.
 */

public class Pin extends Object {
    private String label;
    private PointF location;
    private Bitmap icon;

    public Pin(String label, PointF location, Bitmap icon) {
        initialise(label, location, icon);
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public PointF getLocation() { return location; }
    public void setLocation(PointF location) { this.location = location; }

    public Bitmap getIcon() { return icon; }
    public void setIcon(Bitmap icon) { this.icon = icon; }

    private void initialise(String label, PointF location, Bitmap icon) {
        this.label = label;
        this.location = location;
        this.icon = icon;
    }
}