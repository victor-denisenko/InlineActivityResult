package com.github.florent37.inlineactivityresult.sample;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;

public class MainActivityJava8 extends AppCompatActivity {

    private ImageView resultView;
    private View requestView;
    private View requestIntentSenderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        resultView = findViewById(R.id.resultView);
        requestView = findViewById(R.id.requestView);
        requestIntentSenderView = findViewById(R.id.requestIntentSenderView);

        requestView.setOnClickListener(view -> myMethod(RequestFabric.create(new Intent(MediaStore.ACTION_IMAGE_CAPTURE))));

        requestIntentSenderView.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);

            Request request = RequestFabric.create(pendingIntent.getIntentSender(), null, 0, 0, 0, null);

            myMethod(request);
        });
    }

    private void myMethod(Request request) {
        new InlineActivityResult(this)
                .startForResult(request)
                .onSuccess(result -> {
                    Intent data = result.getData();

                    Bundle extras;

                    if (data != null) {
                        extras = data.getExtras();

                        Bitmap imageBitmap;

                        if (extras != null) {
                            imageBitmap = (Bitmap) extras.get("data");

                            resultView.setImageBitmap(imageBitmap);

                            return;
                        }
                    }

                    Toast.makeText(MainActivityJava8.this, "Cannot show image", Toast.LENGTH_SHORT).show();
                })
                .onFail(result -> {
                    Toast.makeText(MainActivityJava8.this, "Cannot show image", Toast.LENGTH_SHORT).show();
                });
    }
}
