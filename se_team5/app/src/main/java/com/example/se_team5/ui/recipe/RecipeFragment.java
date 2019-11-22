package com.example.se_team5.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.se_team5.R;

public class RecipeFragment extends Fragment {

    private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipe, container, false);


        recipeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

}