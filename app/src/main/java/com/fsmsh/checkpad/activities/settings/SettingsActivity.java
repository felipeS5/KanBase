package com.fsmsh.checkpad.activities.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.TagsBottomSheet;
import com.fsmsh.checkpad.databinding.ActivitySettingsBinding;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.fsmsh.checkpad.util.MyPreferences;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBarSettings);
        getSupportActionBar().setTitle("Configurações");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.lblTemaConfigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temaAtual = MyPreferences.getTema();
                int checkedItem = 0;

                if (temaAtual == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) checkedItem = 0;
                else if (temaAtual == AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 1;
                else if (temaAtual == AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 2;

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
                builder.setTitle("Tema");
                builder.setSingleChoiceItems(new String[]{"Padrão do sistema", "Claro", "Escuro"}, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            int tema = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                            AppCompatDelegate.setDefaultNightMode(tema);
                            MyPreferences.setTema(tema);

                        } else if (i == 1) {
                            int tema = AppCompatDelegate.MODE_NIGHT_NO;
                            AppCompatDelegate.setDefaultNightMode(tema);
                            MyPreferences.setTema(tema);

                        } else if (i == 2) {
                            int tema = AppCompatDelegate.MODE_NIGHT_YES;
                            AppCompatDelegate.setDefaultNightMode(tema);
                            MyPreferences.setTema(tema);

                        }

                        dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton("Cancelar", null);

                builder.show();
            }
        });

    }
}