package com.fsmsh.checkpad.activities.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
        getSupportActionBar().setTitle(R.string.sobre_o_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Linkedin
        findViewById(R.id.imageView7).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/felipe-santos-0571ba2b2/"));
            startActivity(intent);
        });

        // Github
        findViewById(R.id.imageView8).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/felipeS5"));
            startActivity(intent);
        });

        // Site
        Toast.makeText(this, "Não implementado", Toast.LENGTH_SHORT).show();
        /*
        findViewById(R.id.imageView7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
                startActivity(intent);
            }
        });
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verificar se o item selecionado é o botão de voltar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Fecha a Activity atual, voltando para a anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}