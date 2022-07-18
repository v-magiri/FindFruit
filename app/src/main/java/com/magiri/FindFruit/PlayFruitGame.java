package com.magiri.FindFruit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PlayFruitGame extends AppCompatActivity {
    ImageView FruitImageView,backBtn;
    ImageView speechImageView,textImageView;
    private EditText answerEditText;
    TextView FruitImageAnswer,showAnswerTxt,scoreTxt;
    ImageView answerStatus;
    boolean isGivenHint = false;
    AlertDialog wrongDialog,successDialog;
    Handler imageChangeHandler;
    private Button doneBtn,continueBtn;
    MediaPlayer mp;
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
        scoreTxt=findViewById(R.id.scoreTxt);
        showAnswerTxt=findViewById(R.id.answerTxt);
        answerEditText=findViewById(R.id.answerEditTxt);
        answerEditText.setCursorVisible(false);
        continueBtn=findViewById(R.id.continueBtn);
        speechImageView=findViewById(R.id.SpeechIconImageView);
        textImageView=findViewById(R.id.textIconImageView);
        FruitImageAnswer=findViewById(R.id.FruitNameTxt);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count+=1;
                FruitImageView.setImageResource(FruitImageIds[count]);
                answerEditText.setText("");
            }
        });
        doneBtn=findViewById(R.id.submitBtn);
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
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        textImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerEditText.setCursorVisible(true);
            }
        });
        showAnswerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerEditText.setText(FruitLabels[count]);
                isGivenHint=true;
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getAnswer From the EditText
                String Answer=answerEditText.getText().toString();
                validateAnswer(Answer);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SPEECH_REQUEST_CODE){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> Answer=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText(Objects.requireNonNull(Answer).get(0));
            }
        }
    }

    private void validateAnswer(String userAnswer) {
        if(userAnswer.equalsIgnoreCase(FruitLabels[count])){
           handleCorrectAnswer(userAnswer);
        }else{
            handleWrongAnswer(userAnswer);
        }
    }

    private void handleWrongAnswer(String userAnswer) {
        Context context=PlayFruitGame.this;
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.wrong_answer,null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        TextView FruitName=view.findViewById(R.id.FruitNameTxt);
        Button tryAgainBtn=view.findViewById(R.id.tryAgainBtn);
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAlertUpdateUI();
            }
        });
        FruitName.setText(userAnswer);
        mp=MediaPlayer.create(getApplicationContext(),R.raw.wrong_answer_audio);
        mp.start();
        String currentScore=scoreTxt.getText().toString();
        reduceScore(currentScore);
        wrongDialog=alertDialogBuilder.create();
        wrongDialog.show();
    }

    private void handleCorrectAnswer(String userAnswer) {
        Context context=PlayFruitGame.this;
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.correct_alert_dialog,null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        TextView FruitName=view.findViewById(R.id.FruitNameTxt);
        FruitName.setText(userAnswer);
        Button ContinueBtn=view.findViewById(R.id.continueBtn);
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });
        mp= MediaPlayer.create(getApplicationContext(),R.raw.correct_answer_audio);
        mp.start();
        String currentScore=scoreTxt.getText().toString();
        addScore(currentScore);
        successDialog=alertDialogBuilder.create();
        successDialog.show();
        count++;
        imageChangeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FruitImageView.setImageResource(FruitImageIds[count]);
            }
        },2000);
    }

    private void CloseAlertUpdateUI() {
        answerEditText.setText("");
        wrongDialog.dismiss();
    }

    private void updateUI() {
        FruitImageView.setImageResource(FruitImageIds[count]);
        answerEditText.setText("");
        successDialog.dismiss();
        isGivenHint=false;
    }

    private void reduceScore(String currentScore) {
        int Score=Integer.parseInt(currentScore);
        if(Score>0){
            scoreTxt.setText(String.valueOf(--Score));
        }

    }

    private void addScore(String currentScore) {
        if(!isGivenHint){
            int Score=Integer.parseInt(currentScore);
            scoreTxt.setText(String.valueOf(++Score));
        }

    }
}