package com.magiri.hci_assigno;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Image_Analysis extends AppCompatActivity {
    private ImageView capturedImg;
    private Button moreBtn;
    private TextView nameTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analysis);
    }
}