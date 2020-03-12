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

import com.github.florent37.inlineactivityresult.Result;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
import com.github.florent37.inlineactivityresult.rx.RxInlineActivityResult;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class MainActivityRx extends AppCompatActivity {

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

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        setOnClick(requestView, $ -> new RxInlineActivityResult(this).request(intent));


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        setOnClick(requestIntentSenderView, $ -> {
            Request request = RequestFabric.create(pendingIntent.getIntentSender(), null, 0, 0, 0, null);

            return new RxInlineActivityResult(this).request(request);
        });

    }

    void setOnClick(View view, Function<Object, ObservableSource<? extends Result>> mapper) {
        RxView.clicks(view)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(mapper)
                .subscribe(new DisposableObserver<Result>() {
                    @Override
                    public void onNext(Result result) {
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

                        Toast.makeText(MainActivityRx.this, "Cannot show image", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivityRx.this, "Cannot show image", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
