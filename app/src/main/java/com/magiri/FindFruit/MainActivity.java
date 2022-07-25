package com.magiri.FindFruit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    private static final int CAMERA_REQUEST = 24;
    private static final int Camera_Request_CODE=43;
    private static final int Image_Pick_Code=101;
    ImageView backBtn;
    Uri filePath;
    private Button takePicImageView,pickPicImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePicImageView=findViewById(R.id.cameraImageView);
        pickPicImageView=findViewById(R.id.imagePickerBtn);
        backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
        //take a picture using a camera
        takePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        pickPicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReadPermission();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_PERMISSIONS_REQUEST_CAMERA && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
        }
        else{
            Toast toast=Toast.makeText(getApplicationContext(),"Allow Camera Permissions Please",Toast.LENGTH_SHORT);
            View view=toast.getView();
            view.getBackground().setColorFilter(Color.parseColor("#949494"), PorterDuff.Mode.SRC_IN);
            TextView text=view.findViewById(android.R.id.message);
            text.setTextColor(Color.parseColor("#FFFFFF"));
            text.setTextSize(16);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast toast=Toast.makeText(getApplicationContext(),"Allow Camera Permissions Please",Toast.LENGTH_SHORT);
            View view=toast.getView();
            view.getBackground().setColorFilter(Color.parseColor("#949494"), PorterDuff.Mode.SRC_IN);
            TextView text=view.findViewById(android.R.id.message);
            text.setTextColor(Color.parseColor("#FFFFFF"));
            text.setTextSize(16);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            ActivityCompat.requestPermissions(MainActivity.this,  new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
        }
    }

    private void requestReadPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},12);
        }else{
            SelectImage();
        }
    }

    private void SelectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Fruit Image"),Image_Pick_Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int imageSize=224;
        if(requestCode==Image_Pick_Code && resultCode == RESULT_OK && data != null){
            filePath=data.getData();
            Bitmap bitmap=null;
            try{
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
            }catch (IOException e){
                e.printStackTrace();
            }
//            bitmap=Bitmap.createScaledBitmap(bitmap,imageSize,imageSize,false);
            OpenImageAnalysis(bitmap);
        }
        else if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK && data != null){
            Bitmap cameraBitmap=(Bitmap) data.getExtras().get("data");
            int dimensions=Math.min(cameraBitmap.getWidth(),cameraBitmap.getHeight());
            cameraBitmap= ThumbnailUtils.extractThumbnail(cameraBitmap,dimensions,dimensions);
            cameraBitmap=Bitmap.createScaledBitmap(cameraBitmap,imageSize,imageSize,false);
            OpenImageAnalysis(cameraBitmap);
        }
        else{
            Toast toast=Toast.makeText(getApplicationContext(),"Please Choose a Fruit Photo",Toast.LENGTH_SHORT);
            View view=toast.getView();
            view.getBackground().setColorFilter(Color.parseColor("#FD612F"), PorterDuff.Mode.SRC_IN);
            TextView text=view.findViewById(android.R.id.message);
            text.setTextColor(Color.parseColor("#FFFFFF"));
            text.setTextSize(16);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void OpenImageAnalysis(Bitmap bitmap) {
        //startActivity for Image Analysis
        Intent imageAnalysisIntent=new Intent(this,Image_Analysis.class);
         ByteArrayOutputStream bs=new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG,60,bs);
        imageAnalysisIntent.putExtra("FruitImage",bs.toByteArray());
        startActivity(imageAnalysisIntent);
    }
}