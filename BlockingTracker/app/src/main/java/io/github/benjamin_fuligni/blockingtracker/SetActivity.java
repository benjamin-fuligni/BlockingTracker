package io.github.benjamin_fuligni.blockingtracker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;
import android.view.View.OnDragListener;

public class SetActivity extends AppCompatActivity {

    private TextView tvS;
    private TextView tvD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String selected = intent.getStringExtra(ScriptActivity.TEXT_SELECTED);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, selected, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvS = (TextView)  findViewById(R.id.textView2);
        tvS.setOnTouchListener(new TouchListener());
        tvD = (TextView) findViewById(R.id.textView3);
        tvD.setOnDragListener(new dropListener());
    }

    private final class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //clipdata
            CharSequence x = "X";
            ClipData.Item item =  new ClipData.Item(x);
            java.lang.String stuff[] = {"text/plain"};
            ClipDescription cd = new ClipDescription(x, stuff);
            ClipData data = new ClipData(cd, item);

            //ACTION_DOWN refers to the beginning of a touch
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0); //startDrag is deprecated
                return true;
            } else {
                return false;
            }
        }
    }

    private class dropListener implements View.OnDragListener {
        //used for modularity - specific to type of drag but not specific drag
        View draggedView;
        TextView dropped;

        //need to define onDrag method
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    draggedView = (View) event.getLocalState();
                    dropped = (TextView) draggedView;
                    //draggedView.setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    TextView dropTarget = (TextView) v;
                    dropTarget.setText(dropped.getText().toString());
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
