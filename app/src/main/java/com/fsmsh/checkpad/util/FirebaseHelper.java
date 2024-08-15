package com.fsmsh.checkpad.util;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.main.MainActivity;
import com.fsmsh.checkpad.activities.profile.ProfileActivity;
import com.fsmsh.checkpad.model.Credenciais;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private static FirebaseAuth auth;
    private static FirebaseFirestore firestore;
    private static ProfileActivity parentProfile;
    private static MainActivity parentMain;
    private static ListenerRegistration listenerRegistration;

    public FirebaseHelper(ProfileActivity parentProfile) {
        this.parentProfile = parentProfile;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }

    public FirebaseHelper(MainActivity parentMain) {
        this.parentMain = parentMain;
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
                            Toast.makeText(parentProfile, "Sucesso ao logar", Toast.LENGTH_LONG).show();

                            // Pegar dados do usuário remoto
                            DocumentReference documentReference = firestore.collection("users").document(auth.getUid());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    Database.setUsuario(usuario);

                                    // Recupera tarefas do servidor
                                    Database.deleteAllTarefas();            // todo Dar um jeito de adicionar as tarefas e tags do server e local juntas sem conflito de ids
                                    for (Tarefa t : usuario.getTarefas()) {
                                        Database.addTarefa(t);
                                    }

                                    // Recupera tags do servidor
                                    Database.deleteAllTags();
                                    for (String s : usuario.getTags()) {
                                        Database.addTag(s);
                                    }

                                    parentProfile.checarUser();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(parentProfile, "Erro: "+task.getException(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(parentProfile, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                            Usuario usuario = new Usuario();

                            usuario.setTarefas(Database.getTarefas(Database.PROGRESS_TODOS));
                            usuario.setTags(Database.getTags());
                            usuario.setNome(c.getNome());
                            usuario.setEmail(c.getEmail());
                            usuario.setFirestoreDocId(auth.getUid());

                            // Salva os dados no Firestore
                            firestore.collection("users").document(usuario.getFirestoreDocId())
                                    .set(usuario);

                            Database.setUsuario(usuario);

                            parentProfile.checarUser();
                        } else {
                            // If sign in fails, return error message
                            Toast.makeText(parentProfile, "Erro: "+task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    public void deslogar() {
        auth.signOut();
        parentProfile.checarUser();
    }

    public void excluirConta() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(parentProfile);
        View dialog = parentProfile.getLayoutInflater().inflate(R.layout.dialog_confirm_exclude_account, null);
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

                                                Toast.makeText(parentProfile, "Conta deletada", Toast.LENGTH_LONG).show();
                                                parentProfile.checarUser();

                                            }

                                        }

                                    });

                        } else {
                            Toast.makeText(parentProfile, "Verifique sua senha", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


    }

    public static void atualizarRemoto() {
        Usuario usuario = Database.getUsuario();

        usuario.setTarefas(Database.getTarefas(Database.PROGRESS_TODOS));
        usuario.setTags(Database.getTags());
        usuario.setFirestoreDocId(auth.getUid());

        // Salva os dados no Firestore
        firestore.collection("users").document(usuario.getFirestoreDocId())
                .set(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MyPreferences.isSincronizado(true);

                            if (parentProfile != null) {
                                Toast.makeText(parentProfile, "Alterações salvas", Toast.LENGTH_SHORT).show();
                            }

                            atualizarLocal();
                        }
                    }
                });
    }

    public static void atualizarLocal() {
        listenerRegistration = firestore.collection("users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Usuario usuarioRemoto = value.toObject(Usuario.class);

                Database.setUsuario(usuarioRemoto);

                // Recupera tarefas do servidor
                Database.deleteAllTarefas();
                for (Tarefa t : usuarioRemoto.getTarefas()) {
                    Database.addTarefa(t);
                }

                // Recupera tags do servidor
                Database.deleteAllTags();
                for (String s : usuarioRemoto.getTags()) {
                    Database.addTag(s);
                }

            }
        });

    }

    public void removerListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

}
