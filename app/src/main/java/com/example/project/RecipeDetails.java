package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project.model.Ingredient;
import com.example.project.model.Recipe;
import com.example.project.model.Step;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class RecipeDetails extends AppCompatActivity {

    Recipe recipe;
    TextView recipeName;
    LinearLayout layoutIngredient, layoutSteps;
    ImageView ingredientTTS, stepTTS;
    private DatabaseReference ref;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        layoutIngredient = findViewById(R.id.container_ingredient);
        layoutSteps = findViewById(R.id.container_step);
        ref = FirebaseDatabase.getInstance().getReference("Recipes");
        recipeName = findViewById(R.id.recipeName);
        Intent intent = getIntent();
        String str = intent.getStringExtra("name");
        ref.child(str).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipe = snapshot.getValue(Recipe.class);

                if (recipe != null) {
                    String name = recipe.getName();
                    ArrayList<Ingredient> ingredientList = recipe.getIngredientList();
                    ArrayList<Step> stepList = recipe.getStepList();

                    recipeName.setText(name);
                    for (Ingredient i :
                            ingredientList) {
                        addCardIngredient(i.getIngredient(), i.getQuantity(), i.getMeasure());
                    }

                    for (Step s :
                            stepList) {
                        addCardStep(s.getDescription());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ingredientTTS = findViewById(R.id.ingredients_speech);
        stepTTS = findViewById(R.id.steps_speech);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        ingredientTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Ingredient i :
                        recipe.getIngredientList()) {
                    String toSpeak = i.getQuantity() + i.getMeasure() + i.getIngredient();
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        stepTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Step s :
                        recipe.getStepList()) {
                    textToSpeech.speak(s.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    private void addCardIngredient(String name, String quantity, String measure) {
        final View view = getLayoutInflater().inflate(R.layout.detail_ingredient, null);

        TextView nameView = view.findViewById(R.id.ingredient_name);
        TextView quantityView = view.findViewById(R.id.ingredient_quantity);
        TextView measureView = view.findViewById(R.id.ingredient_measure);

        nameView.setText(name);
        quantityView.setText(quantity);
        measureView.setText(measure);


        layoutIngredient.addView(view);
    }

    private void addCardStep(String name) {
        final View view = getLayoutInflater().inflate(R.layout.detail_step, null);

        TextView nameView = view.findViewById(R.id.stepDesc);

        nameView.setText(name);

        layoutSteps.addView(view);
    }
}