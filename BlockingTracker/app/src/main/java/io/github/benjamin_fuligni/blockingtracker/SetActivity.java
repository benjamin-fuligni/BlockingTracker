package io.github.benjamin_fuligni.blockingtracker;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.PinView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
            dbManager.insert("set", "no image chosen");
            imageView.setImage(ImageSource.resource(R.drawable.arena_floorplan));
        } else if (picturePath.equals("no image chosen")) {
            imageView.setImage(ImageSource.resource(R.drawable.arena_floorplan));
        } else {
            imageView.setImage(ImageSource.uri(picturePath));
        }

        FloatingActionButton addPin = (FloatingActionButton) findViewById(R.id.trackBlockingButton);
        addPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                Fragment frag = manager.findFragmentById(R.id.prompt);
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }
                CustomDialogueFragment editNameDialog = new CustomDialogueFragment();
                editNameDialog.show(manager, "fragment_edit_name");

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
            if (index != -1) {
                String data = cursor.getString(2);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Pin>>() {
                }.getType();
                List<Pin> points = gson.fromJson(data, listType);
                for (int p = 0; p < points.size(); p++) {
                    imageView.newPin(points.get(p).getLabel(), points.get(p).getLocation());
                }
            }
            cursor.close();
        }

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                List<Pin> points = imageView.getPins();
                dbManager.insert(currentRecord, gson.toJson(points));
                Cursor cursor = dbManager.fetch();
                cursor.close();
                finish();
            }
        });

        FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean result = imageView.deleteCurrentPin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script, menu);
        menu.findItem(R.id.changeScript).setVisible(false);
        menu.findItem(R.id.scriptInstructions).setVisible(false);
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
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //BUG: couldn't find intent.
                //POSSIBLE FIX: check for i to be null, if so just call this for intent
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                return true;
            case R.id.setInstructions:
                Intent setInstructionsIntent = new Intent(SetActivity.this, SetInstructionsActivity.class);
                startActivity(setInstructionsIntent);
                return true;
            case R.id.about:
                Intent settingsIntent = new Intent(SetActivity.this, AboutActivity.class);
                startActivity(settingsIntent);
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
            dbManager.update("set", picturePath);
        }
    }
}