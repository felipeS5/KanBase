package com.fsmsh.checkpad.activities.profile;

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

    public LoginFragment(ProfileActivity parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText editEmail = view.findViewById(R.id.txt_emai_login);
        EditText editSenha = view.findViewById(R.id.txt_senha_login);

        view.findViewById(R.id.lbl_cadastrar).setOnClickListener(view -> {
            parent.swichFragment();
        });

        view.findViewById(R.id.btn_logar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editEmail.getText().toString().equals("") && !editSenha.getText().toString().equals("")) {
                    Credenciais credenciais = new Credenciais();
                    credenciais.setEmail(editEmail.getText().toString());
                    credenciais.setSenha(editSenha.getText().toString());
                    credenciais.setTipo(Credenciais.TYPE_LOGIN);

                    Toast.makeText(getContext(), credenciais.getEmail() + "\n" + credenciais.getSenha(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Insira as credenciais", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

}