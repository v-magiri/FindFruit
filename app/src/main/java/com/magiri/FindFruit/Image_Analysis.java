package com.magiri.FindFruit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.magiri.FindFruit.ml.Model1;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.schema.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class Image_Analysis extends AppCompatActivity {
    private ImageView FruitImageView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    private static final int CAMERA_REQUEST = 24;
    private static final int Image_Pick_Code=101;
    int imageSize = 32;
    TextToSpeech textToSpeech;
    Uri filePath;
    private ImageView galleryImageView, cameraImageView,backImageView;
    private TextView fruitNameTxt;
    private ImageView speakImageView;
    private TextView readMoreButton;
    private WebView readMoreWebView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analysis);
        FruitImageView=findViewById(R.id.ImageTakenImg);
        galleryImageView=findViewById(R.id.galleryImageView);
        cameraImageView=findViewById(R.id.cameraImageView);
        backImageView=findViewById(R.id.backBtn );
        readMoreButton=findViewById(R.id.readMoreBtn);
        speakImageView=findViewById(R.id.speakerImageView);
        fruitNameTxt=findViewById(R.id.result);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Opening Web Page");
        progressDialog.setCanceledOnTouchOutside(false);
        readMoreWebView=findViewById(R.id.FindFruitWebView);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.forLanguageTag("en-KE"));
                    if(i==TextToSpeech.LANG_MISSING_DATA || i ==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(Image_Analysis.this, "Language Not Supported", Toast.LENGTH_SHORT).show();
                    }
                    textToSpeech.setSpeechRate(1.0F);
                    textToSpeech.setPitch(0.6f);
                    speak();
                }
            }
        });
        textToSpeech.getVoices();
        // create an object textToSpeech and adding features into it
        getData();

        speakImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://en.wikipedia.org/wiki/"+fruitNameTxt.getText().toString();
                readMoreWebView.setWebViewClient(new FindFruitWebClient());
                readMoreWebView.getSettings().setJavaScriptEnabled(true);
                readMoreWebView.loadUrl(url);
                progressDialog.show();
                readMoreWebView.setVisibility(View.VISIBLE);
            }
        });
    }


    private void getData() {
        Bitmap FruitImageBitmap =
                BitmapFactory.
                        decodeByteArray(getIntent().
                        getByteArrayExtra("FruitImage"), 0, getIntent().getByteArrayExtra("FruitImage").length);
        int dimension=Math.min(FruitImageBitmap.getWidth(),FruitImageBitmap.getHeight());
        FruitImageBitmap = ThumbnailUtils.extractThumbnail(FruitImageBitmap,dimension,dimension);
        FruitImageView.setImageBitmap(FruitImageBitmap);
        FruitImageBitmap=Bitmap.createScaledBitmap(FruitImageBitmap,imageSize,imageSize,false);
        classifyImage(FruitImageBitmap);
        textToSpeech.speak(fruitNameTxt.getText().toString(),TextToSpeech.QUEUE_ADD,null);
    }
    public void  classifyImage(Bitmap image){
        try {
            Model1 model = Model1.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            int width=image.getWidth();
            int height=image.getHeight();
            image.getPixels(intValues,0,width,0,0,width,height);
            int pixel = 0;
            // iterate between each pixel in RGB. add them each individually

            for (int i = 0; i < imageSize;i++){
                for (int j=0;j <imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float [] confidences = outputFeature0.getFloatArray();
//            find the class with highest confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length;i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
// Check if % >= 80. if yes, print the suggestion else image could not be classified

            String [] classes = {"Apple","Banana","Orange"};
            fruitNameTxt.setText(classes[maxPos]);

//            if (maxConfidence >= 0.84){
//                fruitNameTxt.setText(classes[maxPos]);
//            }else{
//                fruitNameTxt.setText("Unclassfied ");
//            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void speak() {
        String FruitName=fruitNameTxt.getText().toString();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.speak(FruitName,TextToSpeech.QUEUE_FLUSH,null,null);
        }else{
            textToSpeech.speak(FruitName,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    private class FindFruitWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.dismiss();
        }
    }

}