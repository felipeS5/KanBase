package com.fsmsh.checkpad.activities.about;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityAboutBinding;
import com.fsmsh.checkpad.databinding.ActivitySettingsBinding;

public class AboutActivity extends AppCompatActivity {

    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBarAbout);
        getSupportActionBar().setTitle("Sobre o app");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}