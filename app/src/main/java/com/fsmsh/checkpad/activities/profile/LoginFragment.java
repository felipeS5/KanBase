package com.fsmsh.checkpad.activities.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Credenciais;


public class LoginFragment extends Fragment {

    ProfileActivity parent;
    View view;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        parent = (ProfileActivity) getActivity();

        EditText editEmail = view.findViewById(R.id.txt_emai_login);
        EditText editSenha = view.findViewById(R.id.txt_senha_login);

        view.findViewById(R.id.lbl_cadastrar).setOnClickListener(view -> {
            parent.replaceFragment(new CadastroFragment());
        });

        view.findViewById(R.id.btn_logar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editEmail.getText().toString().equals("") && !editSenha.getText().toString().equals("")) {
                    Credenciais credenciais = new Credenciais();
                    credenciais.setEmail(editEmail.getText().toString());
                    credenciais.setSenha(editSenha.getText().toString());
                    credenciais.setTipo(Credenciais.TYPE_LOGIN);

                    parent.firebaseHelper.logar(credenciais);
                } else {
                    Toast.makeText(getContext(), R.string.insira_as_credenciais, Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.btn_logar_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = parent.firebaseHelper.googleSignInClient.getSignInIntent();
                parent.startActivityForResult(intent, 20);
            }
        });

        return view;
    }

}