package com.example.digitalpictureframe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import androidx.documentfile.provider.DocumentFile;

public class Util
{
    public static boolean folderNotEmpty(Context context, Uri uri)
    {
        context.getContentResolver()
                .takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        List<String> files = getPictures(context, uri);

        return !files.isEmpty();
    }

    public static List<String> getPictures(Context context, Uri uri)
    {
        List<String> fileList = new ArrayList<>();

        DocumentFile[] files = DocumentFile.fromTreeUri(context, uri).listFiles();//

        if (files != null)
        {
            for (DocumentFile file : files)
            {
                if (file.canRead() && file.isFile() && file.getType().contains("image/")) ;
                fileList.add(file.getUri().toString());
            }
        }

        return fileList;
    }
}
