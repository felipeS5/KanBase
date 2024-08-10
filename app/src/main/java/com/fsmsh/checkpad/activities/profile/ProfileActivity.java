package com.fsmsh.checkpad.activities.profile;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.ui.home.FragmentsIniciais;
import com.fsmsh.checkpad.ui.tags.TagsFragment;
import com.fsmsh.checkpad.util.AnimationRes;

public class ProfileActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    CadastroFragment cadastroFragment;
    Fragment fragmentAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setSupportActionBar(findViewById(R.id.toolbarProfile));

    }

    public void replaceFragment(Fragment fragment) {
        // ToDo: remover a opção de foto e basear-se nas imagens da Dribble
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_profile, fragment);
        transaction.commit();

        if (fragment instanceof CadastroFragment) cadastroFragment = (CadastroFragment) fragment;
        if (fragment instanceof LoginFragment) loginFragment = (LoginFragment) fragment;

        fragmentAtual = fragment;
    }

    public void swichFragment() {
        // teste para ver se está logado e ir para a tela de perfil
        if (false) return;

        if (fragmentAtual instanceof CadastroFragment) replaceFragment(new LoginFragment(this));
        else if (fragmentAtual instanceof LoginFragment) replaceFragment(new CadastroFragment(this));

    }

    @Override
    protected void onResume() {
        super.onResume();

        replaceFragment(new CadastroFragment(this));
    }
}