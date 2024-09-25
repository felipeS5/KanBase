package com.fsmsh.kanbase.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.activities.about.AboutActivity;
import com.fsmsh.kanbase.activities.profile.ProfileActivity;
import com.fsmsh.kanbase.databinding.ActivitySettingsBinding;
import com.fsmsh.kanbase.util.Database;
import com.fsmsh.kanbase.util.FirebaseHelper;
import com.fsmsh.kanbase.util.Helper;
import com.fsmsh.kanbase.util.MyPreferences;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseHelper firebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.preConfigs(this);

        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseHelper = new FirebaseHelper(getApplicationContext());
        onResume();

        setSupportActionBar(binding.toolBarSettings);
        getSupportActionBar().setTitle(getString(R.string.configuracoes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Tema
        binding.lblTemaConfigs.setOnClickListener(view -> {
            int temaAtual = MyPreferences.getTema();
            int checkedItem = 0;

            if (temaAtual == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) checkedItem = 0;
            else if (temaAtual == AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 1;
            else if (temaAtual == AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 2;

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
            builder.setTitle(getString(R.string.tema));
            builder.setSingleChoiceItems(new String[]{getString(R.string.padrao_do_sistema), getString(R.string.claro), getString(R.string.escuro)}, checkedItem, new DialogInterface.OnClickListener() {
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

            builder.setPositiveButton(getString(R.string.cancelar), null);

            builder.show();
        });

        // Idioma
        binding.lblIdiomaConfigs.setOnClickListener(view -> {
            String idiomaAtual = MyPreferences.getIdioma();
            int checkedItem = 0;

            if (idiomaAtual.equals("pt_br")) checkedItem = 0;
            else if (idiomaAtual.equals("en")) checkedItem = 1;

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
            builder.setTitle(getString(R.string.idioma));
            builder.setSingleChoiceItems(new String[]{getString(R.string.portugues_brasil), getString(R.string.english)}, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        String lang = "pt_br";
                        Helper.setLocale(SettingsActivity.this, lang);
                        MyPreferences.setIdioma(lang);

                    } else if (i == 1) {
                        String lang = "en";
                        Helper.setLocale(SettingsActivity.this, lang);
                        MyPreferences.setIdioma(lang);

                    }

                    MyPreferences.setPendingRestart(true);
                    dialogInterface.dismiss();
                    finish();
                }
            });

            builder.setPositiveButton(R.string.cancelar, null);

            builder.show();
        });

        // Notificação prévia
        binding.lblNotifyBeforeConfigs.setOnClickListener(view -> {
            int defaultNotify = MyPreferences.getDefaultNotify();
            int checkedItem = 2;

            if (defaultNotify == 60) checkedItem = 0;
            else if (defaultNotify == 30) checkedItem = 1;
            else if (defaultNotify == 15) checkedItem = 2;
            else if (defaultNotify == 5) checkedItem = 3;
            else if (defaultNotify == -1) checkedItem = 4;

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
            builder.setTitle(getString(R.string.notificacao_previa_padrao));
            builder.setSingleChoiceItems(new String[]{getString(R.string._1hr_antes), getString(R.string._30min_antes), getString(R.string._15min_antes), getString(R.string._5min_antes), getString(R.string.desativar_notificacoes)}, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        int notifyBefore = 60;
                        MyPreferences.setDefaultNotify(notifyBefore);

                    } else if (i == 1) {
                        int notifyBefore = 30;
                        MyPreferences.setDefaultNotify(notifyBefore);

                    } else if (i == 2) {
                        int notifyBefore = 15;
                        MyPreferences.setDefaultNotify(notifyBefore);

                    } else if (i == 3) {
                        int notifyBefore = 5;
                        MyPreferences.setDefaultNotify(notifyBefore);

                    } else if (i == 4) {
                        int notifyBefore = -1;
                        MyPreferences.setDefaultNotify(notifyBefore);

                    }

                    dialogInterface.dismiss();
                }
            });

            builder.setPositiveButton(R.string.cancelar, null);

            builder.show();
        });

        // Resumo diário
        binding.lblResumoConfigs.setOnClickListener(view -> {
            DailySumaryBottomSheet dailySumaryBottomSheet = new DailySumaryBottomSheet(SettingsActivity.this, binding);
            dailySumaryBottomSheet.show(getSupportFragmentManager(), DailySumaryBottomSheet.TAG);
        });

        // Perfil
            // Conta não logada
        binding.lblLogarConfigs.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        binding.lblResetTasksConfigs.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
            builder.setTitle(R.string.excluir_tarefas);
            builder.setMessage(R.string.voce_quer_realmente_excluir_todas_as_tarefas_q);

            builder.setPositiveButton(R.string.excluir, (dialogInterface, i) -> {
                Database.deleteAllTarefas();
            });

            builder.setNegativeButton(R.string.cancelar, null);

            builder.show();
        });

            // Conta logada
        binding.lblPerfilConfigs.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        binding.lblDeslogarConfigs.setOnClickListener(view -> {
            firebaseHelper.deslogar();

            onResume();
        });

        // Sobre o app
        binding.lblAboutConfigs.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firebaseHelper.getFirebaseUser() == null) {
            binding.conteinerLogado.setVisibility(View.GONE);
            binding.conteinerDeslogado.setVisibility(View.VISIBLE);
        } else {
            binding.conteinerDeslogado.setVisibility(View.GONE);
            binding.conteinerLogado.setVisibility(View.VISIBLE);
        }
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