package com.zagavideodown.app.tiksave.downloaders;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zagavideodown.app.R;
import com.zagavideodown.app.tiksave.listener.AsyncResponse;
import com.zagavideodown.app.utils.Utils;

import org.jsoup.nodes.Document;


@SuppressLint("StaticFieldLeak")
public abstract class Base extends AsyncTask<String, Void, Document> {

    public AsyncResponse delegate;
    Document roposoDoc;
    String videoUrl = "";
    Context mainContext;
    ProgressDialog progressDialog;


    public Base(Context c, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        Utils.createFileFolder();
        mainContext = c;
        progressDialog = new ProgressDialog(mainContext);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progess_dailog_vertical);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    protected void DownloadFailed() {

        progressDialog.dismiss();
        Toast.makeText(mainContext, "Try later", Toast.LENGTH_SHORT).show();
    }

    protected void DownloadFailed(String Message) {
        progressDialog.dismiss();
        try {
            Toast.makeText(mainContext, Message, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
        }

    }


}

