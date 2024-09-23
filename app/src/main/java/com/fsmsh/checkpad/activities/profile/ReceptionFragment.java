package com.fsmsh.checkpad.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fsmsh.checkpad.R;

public class ReceptionFragment extends Fragment {

    public ReceptionFragment() {
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reception, container, false);

        ProfileActivity parent = (ProfileActivity) getActivity();

        view.findViewById(R.id.button2).setOnClickListener(view1 -> {
            parent.replaceFragment(new CadastroFragment());
        });

        view.findViewById(R.id.button3).setOnClickListener(view1 -> {
            parent.replaceFragment(new LoginFragment());
        });

        view.findViewById(R.id.btn_logar_google).setOnClickListener(view1 -> {
            Log.d("TAG", "Tela: "+parent.firebaseHelper.telaAtual);
            Intent intent = parent.firebaseHelper.googleSignInClient.getSignInIntent();
            parent.startActivityForResult(intent, 20);
        });

        return view;

    }
}