package com.example.digitalpictureframe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class DigitalPictureFrame extends AppCompatActivity
{
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_FOLDER_PICKER = 2;
    private static Settings settings;
    private boolean isAlbumSet = false;
    private List<String> pictures;
    private ImageView pictureFrame;
    private ImageView pictureFrameBlur;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_picture_frame);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pictureFrame = findViewById(R.id.pictureFrame);
        pictureFrameBlur = findViewById(R.id.pictureFrameBlur);
        loadSettings();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);

        registerForContextMenu(pictureFrame);
        pictureFrame.setOnClickListener(this::nextPhoto);
    }

    private void loadSettings()
    {
        settings = new Settings(this);
        if (settings.isSet())
            setAlbum(settings.getUri());
    }

    private boolean setAlbum(Uri uri)
    {
        getContentResolver()
                .takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        List<String> files = Util.getPictures(this, uri);

        if (!files.isEmpty())
        {
            pictures = files;
            isAlbumSet = true;
            setRandomPhoto();
//            setTimer();

            return true;
        }

        return false;
    }

    private void setTimer()
    {
        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask()
                                  {
                                      @Override
                                      public void run()
                                      {
                                          setRandomPhoto();
                                      }
                                  },
                0,
                3000);
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

    public void nextPhoto(View view)
    {
        if (isAlbumSet)
            setRandomPhoto();
        else
            lookForAlbum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_FOLDER_PICKER && resultCode == Activity.RESULT_OK)
        {
            if (data != null)
            {
                Uri treeUri = data.getData();

                if (setAlbum(treeUri))
                {
                    settings.setFolder(treeUri);
                    settings.save(this);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setRandomPhoto()
    {
        Random random = new Random();

        Uri pictureUri = Uri.parse(pictures.get(random.nextInt(pictures.size())));

        pictureFrame.setImageURI(pictureUri);
        Glide.with(this)
                .load(pictureUri)
                .apply(bitmapTransform(new BlurTransformation(25, 3)))
                .into(pictureFrameBlur);
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