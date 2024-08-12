package com.fsmsh.checkpad.activities.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.model.Usuario;
import com.fsmsh.checkpad.util.Database;

public class AccountFragment extends Fragment {

    View view;
    ProfileActivity parent;

    public AccountFragment(ProfileActivity parent) {
        this.parent = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        view.findViewById(R.id.btn_deslogar_perfil)
                .setOnClickListener(view -> parent.firebaseHelper.deslogar());
        view.findViewById(R.id.btn_deletar_conta_perfil)
                .setOnClickListener(view -> parent.firebaseHelper.excluirConta());

        EditText txtNome = view.findViewById(R.id.txt_nome_perfil);
        txtNome.setText(Database.getUsuario().getNome());

        EditText txtEmail = view.findViewById(R.id.txt_email_perfil);
        txtEmail.setText(Database.getUsuario().getEmail());

        return view;
    }
}