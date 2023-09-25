package com.example.digitalpictureframe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class DigitalPictureFrame extends AppCompatActivity
{
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;


    public ImageView pictureFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_picture_frame);

        pictureFrame = findViewById(R.id.pictureFrame);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);


        // TODO: 9/24/2023 load data from storage, this is later, for now ill request each time

        registerForContextMenu(pictureFrame);
        pictureFrame.setOnClickListener(this::nextPhoto);
    }

    public void lookForAlbum()
    {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//
//        startActivityForResult(intent, 2);

        // TODO: 9/24/2023 for now ill just go to a single location


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nextPhoto(View view)
    {
        Toast.makeText(DigitalPictureFrame.this, "next", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION)
        {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                onStop();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.album:
                lookForAlbum();

            default:
                return super.onContextItemSelected(item);
        }
    }
}