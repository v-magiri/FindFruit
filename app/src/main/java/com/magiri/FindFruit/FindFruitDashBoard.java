package com.magiri.FindFruit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;

public class FindFruitDashBoard extends AppCompatActivity {
    private static final String TAG = "GameScore";
    private MaterialCardView searchFruitCardView,playFruitGameCard;
    private Button searchFruitBtn,playFruitGameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fruit_dash_board);
        searchFruitBtn=findViewById(R.id.searchFruitBtn);
        playFruitGameBtn=findViewById(R.id.playGameBtn);
        searchFruitCardView=findViewById(R.id.search_Fruit_Card);
        playFruitGameCard=findViewById(R.id.playFruitGame);
        searchFruitBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        searchFruitCardView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        playFruitGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences FindFruit=getSharedPreferences("com.magiri.FindFruit.FruitGame", Context.MODE_PRIVATE);
                int score=FindFruit.getInt("FruitGame_Score",0);
                Log.d(TAG, "GetGameScore: "+score);
                startActivity(new Intent(getApplicationContext(),PlayFruitGame.class));
            }
        });

        playFruitGameBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),PlayFruitGame.class)));

    }
}