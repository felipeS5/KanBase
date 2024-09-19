package com.fsmsh.checkpad.activities.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Usuario;
import com.fsmsh.checkpad.util.Database;

public class AccountFragment extends Fragment {

    View view;
    ProfileActivity parent;

    public AccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        parent = (ProfileActivity) getActivity();

        view.findViewById(R.id.btn_deslogar_perfil)
                .setOnClickListener(view -> parent.firebaseHelper.deslogar());
        view.findViewById(R.id.btn_deletar_conta_perfil)
                .setOnClickListener(view -> parent.firebaseHelper.excluirConta());

        EditText txtNome = view.findViewById(R.id.txt_nome_perfil);
        txtNome.setText(Database.getUsuario().getNome());

        EditText txtEmail = view.findViewById(R.id.txt_email_perfil);
        txtEmail.setText(Database.getUsuario().getEmail());
        txtEmail.setEnabled(false);

        txtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtNome.getText().toString().equals("") && !txtEmail.getText().toString().equals("")) {
                    Usuario usuarioLocal = new Usuario();
                    usuarioLocal.setNome(txtNome.getText().toString());
                    usuarioLocal.setEmail(Database.getUsuario().getEmail());
                    usuarioLocal.setLoginType(Database.getUsuario().getLoginType());

                    Database.setUsuario(usuarioLocal);
                    parent.firebaseHelper.atualizarRemoto();

                } else {
                    Toast.makeText(getContext(), R.string.insira_as_credenciais, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}