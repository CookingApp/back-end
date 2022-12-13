package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.Ingredient;
import com.example.project.model.Recipe;
import com.example.project.model.Step;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class AddRecipe extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Recipe recipe;
    TextView recipeName;
    Step step;
    Ingredient ingredient;
    Button addIngredient, confirm, addStep;
    AlertDialog ingredientDialog, stepDialog, confirmDialog;
    LinearLayout layoutIngredient, layoutSteps;
    ArrayList<Ingredient> ingredientsList;
    ArrayList<Step> stepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mAuth = FirebaseAuth.getInstance();

        recipeName = findViewById(R.id.txt_recipe_name);
        confirm = findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.show();
            }
        });

        addStep = findViewById(R.id.add_step);
        layoutSteps = findViewById(R.id.container_step);

        buildDialogStep();
        ingredientsList = new ArrayList<>();
        stepsList = new ArrayList<>();
        addIngredient = findViewById(R.id.add_ingredient);
        layoutIngredient = findViewById(R.id.container_ingredient);

        buildDialogIngredient();

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientDialog.show();
            }
        });
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepDialog.show();
            }
        });
        buildDialogConfirm();
    }

    private void buildDialogIngredient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_ingredient, null);

        EditText ingredientName = view.findViewById(R.id.ingredient_name);
        EditText ingredientQuantity = view.findViewById(R.id.ingredient_quantity);
        Spinner ingredientMeasure = view.findViewById(R.id.measure_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>
                (AddRecipe.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.measures));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientMeasure.setAdapter(myAdapter);

        builder.setView(view);
        builder.setTitle("Enter name").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = ingredientName.getText().toString().trim();
                String quantity = ingredientQuantity.getText().toString().trim();
                String measure = ingredientMeasure.getSelectedItem().toString().trim();
                if (name.isEmpty()) {
                    ingredientName.setError("Ingredient name is required!");
                    ingredientName.requestFocus();
                    return;
                }
                if (quantity.isEmpty()) {
                    ingredientQuantity.setError("Ingredient quantity is required!");
                    ingredientQuantity.requestFocus();
                    return;
                }

                addCardIngredient(name, quantity, measure);
                ingredient = new Ingredient(name, quantity, measure);
                ingredientsList.add(ingredient);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ingredientDialog = builder.create();
    }

    private void addCardIngredient(String name, String quantity, String measure) {
        final View view = getLayoutInflater().inflate(R.layout.item_ingredient, null);

        TextView nameView = view.findViewById(R.id.ingredient_name);
        TextView quantityView = view.findViewById(R.id.ingredient_quantity);
        TextView measureView = view.findViewById(R.id.ingredient_measure);

        ImageView delete = view.findViewById(R.id.delete);

        nameView.setText(name);
        quantityView.setText(quantity);
        measureView.setText(measure);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutIngredient.removeView(view);
                for (Ingredient i :
                        ingredientsList) {
                    if (i.getIngredient() == name) ingredientsList.remove(i);
                }
            }
        });

        layoutIngredient.addView(view);
    }

    private void buildDialogStep() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_step, null);

        EditText stepDesc = view.findViewById(R.id.step_desc);

        builder.setView(view);
        builder.setTitle("Enter name").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String desc = stepDesc.getText().toString().trim();
                if (desc.isEmpty()) {
                    stepDesc.setError("Description is required!");
                    stepDesc.requestFocus();
                    return;
                }
                addCardStep(desc);
                step = new Step();
                step.setDescription(desc);
                stepsList.add(step);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        stepDialog = builder.create();
    }

    public void buildDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to add this recipe?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (recipeName.getText().toString().trim().isEmpty()) {
                    recipeName.setError("Recipe name is required!");
                    recipeName.requestFocus();
                    return;
                }
                recipe = new Recipe(recipeName.getText().toString().trim(), ingredientsList, stepsList);
                FirebaseDatabase.getInstance().getReference("Recipes")
                        .child(recipe.getName()).setValue(recipe)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddRecipe.this, "The recipe has been registered successfully!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Acceuil.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(AddRecipe.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        confirmDialog = builder.create();
    }

    private void addCardStep(String name) {
        final View view = getLayoutInflater().inflate(R.layout.item_step, null);

        TextView nameView = view.findViewById(R.id.text);
        Button delete = view.findViewById(R.id.delete);

        nameView.setText(name);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSteps.removeView(view);
                for (Step s :
                        stepsList) {
                    if (Objects.equals(s.getDescription(), name)) stepsList.remove(s);
                }
            }
        });

        layoutSteps.addView(view);
    }


}