package com.fsmsh.kanbase.activities.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.model.Credenciais;

public class CadastroFragment extends Fragment {

    View view;
    ProfileActivity parent;

    public CadastroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cadastro, container, false);

        parent = (ProfileActivity) getActivity();

        EditText editNome = view.findViewById(R.id.txt_nome_cadastro);
        EditText editEmail = view.findViewById(R.id.txt_email_cadastro);
        EditText editSenha = view.findViewById(R.id.txt_senha_cadastro);

        view.findViewById(R.id.lbl_logar).setOnClickListener(view -> {
            parent.replaceFragment(new LoginFragment());
        });

        view.findViewById(R.id.btn_cadastrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editNome.getText().toString().equals("") && !editEmail.getText().toString().equals("") && !editSenha.getText().toString().equals("")) {
                    Credenciais credenciais = new Credenciais();
                    credenciais.setNome(editNome.getText().toString());
                    credenciais.setEmail(editEmail.getText().toString());
                    credenciais.setSenha(editSenha.getText().toString());
                    credenciais.setTipo(Credenciais.TYPE_REGISTER);

                    parent.firebaseHelper.criarConta(credenciais);
                } else {
                    Toast.makeText(getContext(), R.string.insira_as_credenciais, Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.btn_cadastrar_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = parent.firebaseHelper.googleSignInClient.getSignInIntent();
                parent.startActivityForResult(intent, 20);
            }
        });

        return view;
    }

}