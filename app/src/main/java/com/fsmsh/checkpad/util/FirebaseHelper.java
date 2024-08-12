package com.fsmsh.checkpad.util;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.TagsBottomSheet;
import com.fsmsh.checkpad.activities.profile.ProfileActivity;
import com.fsmsh.checkpad.model.Credenciais;
import com.fsmsh.checkpad.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class FirebaseHelper {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    ProfileActivity parent;

    public FirebaseHelper(ProfileActivity parent) {
        this.parent = parent;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }


    public void logar(Credenciais c) {

        auth.signInWithEmailAndPassword(c.getEmail(), c.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(parent, "Sucesso ao logar", Toast.LENGTH_LONG).show();

                            Usuario usuario = new Usuario();
                            usuario.setNome(c.getNome());
                            usuario.setEmail(c.getEmail());

                            Database.setUsuario(usuario);

                            parent.checarUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(parent, "Erro: "+task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void criarConta(Credenciais c) {

        auth.createUserWithEmailAndPassword(c.getEmail(), c.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(parent, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                            Usuario usuario = new Usuario();

                            usuario.setTarefas(Database.getTarefas(Database.PROGRESS_TODOS));
                            usuario.setNome(c.getNome());
                            usuario.setEmail(c.getEmail());
                            usuario.setFirestoreDocId(auth.getUid());

                            // Salva os dados no Firestore
                            firestore.collection("users").document(usuario.getFirestoreDocId())
                                    .set(usuario);

                            Database.setUsuario(usuario);

                            parent.checarUser();
                        } else {
                            // If sign in fails, return error message
                            Toast.makeText(parent, "Erro: "+task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    public void deslogar() {
        auth.signOut();
        parent.checarUser();
    }

    public void excluirConta() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(parent);
        View dialog = parent.getLayoutInflater().inflate(R.layout.dialog_confirm_exclude_account, null);
        builder.setView(dialog);

        AlertDialog alertDialog = builder.show();


        dialog.findViewById(R.id.btn_confirmar_exclusao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText senha = dialog.findViewById(R.id.txt_senha_dialog_exclude_account);

                AuthCredential authCredential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), senha.getText().toString());

                auth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();

                            String userUid = auth.getUid();

                            auth.getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                firestore.collection("users").document(userUid)
                                                        .delete();

                                                Toast.makeText(parent, "Conta deletada", Toast.LENGTH_LONG).show();
                                                parent.checarUser();

                                            }

                                        }

                                    });

                        } else {
                            Toast.makeText(parent, "Verifique sua senha", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


    }

}
