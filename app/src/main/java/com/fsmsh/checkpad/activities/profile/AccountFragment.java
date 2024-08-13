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

        view.findViewById(R.id.btn_salvar_alteracoes_perfil)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!txtNome.getText().toString().equals("") && !txtEmail.getText().toString().equals("")) {
                            Usuario usuarioLocal = new Usuario();
                            usuarioLocal.setNome(txtNome.getText().toString());
                            usuarioLocal.setEmail(txtEmail.getText().toString());

                            Database.setUsuario(usuarioLocal);
                            parent.firebaseHelper.atualizarDados();

                        } else {
                            Toast.makeText(getContext(), "Insira as credenciais", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }
}