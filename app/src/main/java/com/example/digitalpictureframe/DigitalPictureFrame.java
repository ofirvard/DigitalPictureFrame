package com.example.digitalpictureframe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DigitalPictureFrame extends AppCompatActivity
{
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_FOLDER_PICKER = 2;
    private boolean isAlbumSet = false;
    private List<DocumentFile> pictures;

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
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, REQUEST_FOLDER_PICKER);

        // TODO: 9/24/2023 for now ill just go to a single location


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_FOLDER_PICKER && resultCode == Activity.RESULT_OK)
        {
            if (data != null)
            {
                Uri treeUri = data.getData();
                // Handle the selected directory using the treeUri
                // You can save this treeUri to access the directory later
                getContentResolver()
                        .takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                List<DocumentFile> files = getPictures(treeUri);
                if (!files.isEmpty())
                {
                    pictures = files;
                    isAlbumSet = true;
                    setRandomPhoto();
                }

                Toast.makeText(DigitalPictureFrame.this, "album", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRandomPhoto()
    {
        Random random = new Random();

        pictureFrame.setImageURI(pictures.get(random.nextInt(pictures.size())).getUri());
    }

    private List<DocumentFile> getPictures(Uri treeUri)
    {
        List<DocumentFile> fileList = new ArrayList<>();

        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        DocumentFile[] files = pickedDir.listFiles();

        if (files != null)
        {
            for (DocumentFile file : files)
            {
                if (file.canRead() && file.isFile() && file.getType().contains("image/"))
                    fileList.add(file);
            }
        }

        return fileList;
    }

    public void nextPhoto(View view)
    {
        if (isAlbumSet)
        {
            Toast.makeText(DigitalPictureFrame.this, "next", Toast.LENGTH_SHORT).show();
            setRandomPhoto();
        }
        else
            lookForAlbum();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
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