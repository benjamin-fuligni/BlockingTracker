package io.github.benjamin_fuligni.blockingtracker;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;
import android.view.View.OnDragListener;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.PinView;
import android.graphics.*;
import android.util.AttributeSet;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.sqlite.*;
import android.provider.BaseColumns;

public class SetActivity extends AppCompatActivity {

    private TextView tvS;
    private TextView tvD;
    private float xPos;
    private float yPos;
    private PointF point;
    private PinView pv;
    private DBManager dbManager;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String selected = intent.getStringExtra(ScriptActivity.TEXT_SELECTED);
        final String number = intent.getStringExtra(ScriptActivity.NUMBER_INSERT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, selected + "   database cleared", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                dbManager.deleteAll();
            }
        });

        /*
        tvS = (TextView)  findViewById(R.id.textView2);
        tvS.setOnTouchListener(new TouchListener());
        tvD = (TextView) findViewById(R.id.textView3);
        tvD.setOnDragListener(new dropListener());
        */

        final PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.balch));
        imageView.newPin("Ophelia", new PointF(300f, 300f));
        imageView.newPin("Hamlet", new PointF(1300f, 1300f));

        dbManager = new DBManager(this);
        dbManager.open();

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<PointF> points = imageView.getPins();
                dbManager.insert(number, points.toString());
                Cursor cursor = dbManager.fetch();
                Log.d("in SetActivity", DatabaseUtils.dumpCursorToString(cursor));
                int count = cursor.getCount();
                cursor.close();
                String dbCount = ((Integer) count).toString();

                Snackbar.make(view, dbCount, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

    }

    /*
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
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                xPos = motionEvent.getX();
                yPos = motionEvent.getY();
                Log.v("x", String.valueOf(xPos));
                Log.v("y", String.valueOf(yPos));
                point = new PointF(xPos, yPos);
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
                    pv.setPinLocation(0, point);
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
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.changeFloorplan:
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to read the contacts
                    }
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique
                }
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("*** In Set Activity", picturePath);
            PinView imageView = (PinView) findViewById(R.id.imageView);
            imageView.setImage(ImageSource.uri(picturePath));
        }
    }
}
