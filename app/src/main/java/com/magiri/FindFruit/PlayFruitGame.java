package com.magiri.FindFruit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PlayFruitGame extends AppCompatActivity {
    ImageView FruitImageView,backBtn;
    ImageView speechImageView,textImageView;
    TextView FruitImageAnswer;
    Handler imageChangeHandler;
    private static final int SPEECH_REQUEST_CODE=1;
    int FruitImageIds[]={R.drawable.banana_img,R.drawable.apple_img,R.drawable.passion_img
            ,R.drawable.pear_img,R.drawable.mango_img,R.drawable.mango_img1,R.drawable.pawpaw_img,
            R.drawable.red_passion_img};
    String FruitLabels[]={"Banana","Apple","Passion Fruit","Pears","Mango","Mango","PawPaw","Passion Fruit"};
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_fruit_game);
        backBtn=findViewById(R.id.FruitGameBackBtn);
        backBtn.setOnClickListener(v -> finish());
        FruitImageView=findViewById(R.id.fruitImg);
        imageChangeHandler=new Handler();
        speechImageView=findViewById(R.id.SpeechIconImageView);
        textImageView=findViewById(R.id.textIconImageView);
        FruitImageAnswer=findViewById(R.id.FruitImageViewAnswer);
        FruitImageView.setImageResource(FruitImageIds[0]);
        speechImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Identify the Fruit");
                try {
                    startActivityForResult(speechIntent,SPEECH_REQUEST_CODE);
                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SPEECH_REQUEST_CODE){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> Answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                displayAnswer(Objects.requireNonNull(Answer).get(0));
            }
        }
    }

    private void displayAnswer(String userAnswer) {
        if(userAnswer.equalsIgnoreCase(FruitLabels[count])){
            FruitImageAnswer.setText(userAnswer.toUpperCase(Locale.ROOT));
            count++;
            imageChangeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FruitImageView.setImageResource(FruitImageIds[count]);
                }
            },2000);

        }else{
            FruitImageView.setImageResource(FruitImageIds[count]);
            count++;
        }
    }
}