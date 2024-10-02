package com.fsmsh.kanbase.activities.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.util.FirebaseHelper;
import com.fsmsh.kanbase.util.Helper;

public class ProfileActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.preConfigs(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(findViewById(R.id.toolbarProfile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.conta);

        firebaseHelper = new FirebaseHelper(this);

    }

    public void checarUser() {
        if (firebaseHelper.getFirebaseUser() != null) {
            replaceFragment(new AccountFragment());
        } else {
            replaceFragment(new ReceptionFragment());
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_profile, fragment);
        transaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        checarUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 20) {
            firebaseHelper.logarComGoogle(data);
        } else if (requestCode == 21) {
            firebaseHelper.excluirContaGoogle(data);
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