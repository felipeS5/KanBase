package com.fsmsh.checkpad.activities.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Credenciais;
import com.fsmsh.checkpad.ui.home.FragmentsIniciais;
import com.fsmsh.checkpad.ui.tags.TagsFragment;
import com.fsmsh.checkpad.util.AnimationRes;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;

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
}