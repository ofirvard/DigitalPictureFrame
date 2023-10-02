package com.example.digitalpictureframe;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings
{
    private final String FILENAME = "settings.json";
    private String folder;

    Settings(Context context)
    {
        Settings oldSettings = load(context);
        if (oldSettings != null && Util.folderNotEmpty(context, oldSettings.getUri()))
        {
            this.folder = oldSettings.folder;
        }
    }

    public void setFolder(Uri folder)
    {
        this.folder = folder.toString();
    }

    public void save(Context context)
    {
        Gson gson = new Gson();
        String data = gson.toJson(this);

        try
        {
            FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e)
        {
            Log.d("ERROR", "Failed to save: " + data, e);
        }
    }

    public Settings load(Context context)
    {
        if (new File(context.getFilesDir(), FILENAME).exists())
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILENAME))))
            {
                Gson gson = new Gson();

                StringBuilder builder = new StringBuilder();
                String aux;
                while ((aux = reader.readLine()) != null)
                {
                    builder.append(aux);
                }

                return gson.fromJson(builder.toString(), Settings.class);
            } catch (IOException e)
            {
                Log.e("Load", e.toString());
            }
        return null;
    }

    public boolean isSet()
    {
        return folder != null;
    }

    public Uri getUri()
    {
        return Uri.parse(folder);
    }
}
