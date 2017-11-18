package io.github.benjamin_fuligni.blockingtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
    private Boolean selected;
    private int id;

    public Pin(int id, String label, PointF location, Bitmap icon) {
        initialise(id, label, location, icon);
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public PointF getLocation() { return location; }
    public void setLocation(PointF location) { this.location = location; }

    public Bitmap getIcon() { return icon; }
    public void setIcon(Bitmap icon) { this.icon = icon; }

    public int getPinId() { return id; }
    public void setPinId(int id) { this.id = id; }

    public Boolean isSelected() { return selected; }
    public void setSelected(Boolean isSelected) { this.selected = isSelected; }

    private void initialise(int id, String label, PointF location, Bitmap icon) {
        this.label = label;
        this.location = location;
        this.icon = icon;
        this.selected = false;
        this.id = id;
    }
}