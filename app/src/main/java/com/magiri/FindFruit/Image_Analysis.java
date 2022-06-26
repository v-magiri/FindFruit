package com.magiri.FindFruit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Image_Analysis extends AppCompatActivity {
    private ImageView FruitImageView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    private static final int CAMERA_REQUEST = 24;
    private static final int Image_Pick_Code=101;
    Uri filePath;
    private ImageView galleryImageView, cameraImageView,backImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analysis);
        FruitImageView=findViewById(R.id.ImageTakenImg);
        galleryImageView=findViewById(R.id.galleryImageView);
        cameraImageView=findViewById(R.id.cameraImageView);
        backImageView=findViewById(R.id.backBtn );
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
        galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Image_Analysis.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Image_Analysis.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},12);
                }else{
                    SelectImage();
                }
            }
        });
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
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
            ActivityCompat.requestPermissions(Image_Analysis.this,  new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
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
        if(requestCode==Image_Pick_Code && resultCode == RESULT_OK && data != null){
            filePath=data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
//                OpenImageAnalysis(bitmap);
                displayImage(bitmap);
//                FruitImageView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK && data!=null){
                Bitmap cameraBitmap=(Bitmap) data.getExtras().get("data");
//                FruitImageView.setImageBitmap(cameraBitmap);
                displayImage(cameraBitmap);
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

    private void displayImage(Bitmap FruitImageBitmap) {
        displayBottomSheet();
        FruitImageView.setImageBitmap(FruitImageBitmap);
    }

    private void displayBottomSheet() {

    }

    private void getData() {
        Bitmap FruitImageBitmap =
                BitmapFactory.
                        decodeByteArray(getIntent().
                        getByteArrayExtra("FruitImage"), 0, getIntent().getByteArrayExtra("FruitImage").length);
        FruitImageView.setImageBitmap(FruitImageBitmap);

    }
}