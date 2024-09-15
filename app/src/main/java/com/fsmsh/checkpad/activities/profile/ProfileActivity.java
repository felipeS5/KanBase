package com.fsmsh.checkpad.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.util.FirebaseHelper;

public class ProfileActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    CadastroFragment cadastroFragment;
    AccountFragment accountFragment;
    Fragment fragmentAtual;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(findViewById(R.id.toolbarProfile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseHelper = new FirebaseHelper(this);

    }

    public void checarUser() {
        if (firebaseHelper.getFirebaseUser() != null) {
            replaceFragment(new AccountFragment(this));
        } else {
            replaceFragment(new CadastroFragment(this));
        }
    }

    public void replaceFragment(Fragment fragment) {
        // ToDo: remover a opção de foto e basear-se nas imagens da Dribble
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_profile, fragment);
        transaction.commit();

        if (fragment instanceof CadastroFragment) cadastroFragment = (CadastroFragment) fragment;
        if (fragment instanceof LoginFragment) loginFragment = (LoginFragment) fragment;
        if (fragment instanceof AccountFragment) accountFragment = (AccountFragment) fragment;

        fragmentAtual = fragment;
    }

    public void swichFragment() {

        if (fragmentAtual instanceof CadastroFragment) replaceFragment(new LoginFragment(this));
        else if (fragmentAtual instanceof LoginFragment) replaceFragment(new CadastroFragment(this));

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