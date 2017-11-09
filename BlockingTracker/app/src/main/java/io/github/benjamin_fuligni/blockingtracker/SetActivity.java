package io.github.benjamin_fuligni.blockingtracker;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.PinView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.graphics.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.lang.reflect.Type;
import java.util.List;

public class SetActivity extends AppCompatActivity {

    private PinView imageView;
    private DBManager dbManager;
    private String currentRecord;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_TEXT_GET = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String selected = intent.getStringExtra(ScriptActivity.TEXT_SELECTED);
        final String number = intent.getStringExtra(ScriptActivity.NUMBER_INSERT);
        int dbTitle = intent.getIntExtra(ScriptActivity.DATABASE_INSERT, 0);

        dbManager = new DBManager(getApplicationContext());
        dbManager.open();

        imageView = (PinView) findViewById(R.id.imageView);
        String picturePath = dbManager.get("set");
        if (picturePath == null) {
            imageView.setImage(ImageSource.resource(R.drawable.balch));
        } else {
            imageView.setImage(ImageSource.uri(picturePath));
        }

        FloatingActionButton addPin = (FloatingActionButton) findViewById(R.id.addPin);
        addPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.newPin("Ophelia", new PointF(100f, 100f));
            }
        });

        currentRecord = number;
        if (dbTitle != 0) {
            Cursor cursor = dbManager.fetch();
            int index = -1;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (dbTitle == cursor.getInt(1)) {
                    index = 1;
                    currentRecord = String.valueOf(dbTitle);
                    break;
                }
                cursor.move(1);
            }
            if (index == -1) {
                imageView.newPin("Ophelia", new PointF(300f, 300f));
                imageView.newPin("Hamlet", new PointF(1300f, 1300f));
            } else {
                String data = cursor.getString(2);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PointF>>() {
                }.getType();
                List<PointF> points = gson.fromJson(data, listType);
                for (int p = 0; p < points.size(); p++) {
                    imageView.newPin("Ophelia", points.get(p));
                }
            }
            cursor.close();
        }

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                List<PointF> points = imageView.getPins();
                dbManager.insert(currentRecord, gson.toJson(points));
                Cursor cursor = dbManager.fetch();
                cursor.close();
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
        menu.findItem(R.id.changeScript).setVisible(false);
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

            PinView imageView = (PinView) findViewById(R.id.imageView);
            imageView.setImage(ImageSource.uri(picturePath));
            dbManager.insert("set", picturePath);
        }
    }
}