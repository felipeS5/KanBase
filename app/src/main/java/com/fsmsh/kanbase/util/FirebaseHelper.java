package com.fsmsh.kanbase.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.activities.main.MainActivity;
import com.fsmsh.kanbase.activities.profile.ProfileActivity;
import com.fsmsh.kanbase.model.Credenciais;
import com.fsmsh.kanbase.model.Tarefa;
import com.fsmsh.kanbase.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FirebaseHelper {
    public int ACTIVITY_MAIN = 0;
    public int ACTIVITY_PROFILE = 1;
    public int ACTIVITY_GENERICA = -1;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private static ListenerRegistration listenerRegistration;
    public GoogleSignInClient googleSignInClient;
    private Context context;
    public int telaAtual;

    private AlertDialog alertDialog;

    public FirebaseHelper(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (context instanceof MainActivity) telaAtual = ACTIVITY_MAIN;
        else if (context instanceof ProfileActivity) telaAtual = ACTIVITY_PROFILE;
        else if (context instanceof Context) telaAtual = ACTIVITY_GENERICA;

        if (telaAtual == ACTIVITY_PROFILE) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        }
    }


    public void salvarNovaConta(String nome, String email, String tipo) {
        Usuario usuario = new Usuario();

        usuario.setTarefas(Database.getTarefas(Database.PROGRESS_TODOS));
        usuario.setTags(Database.getTags());
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLoginType(tipo);
        usuario.setFirestoreDocId(auth.getUid());

        Database.setUsuario(usuario);

        // Salva os dados no Firestore
        firestore.collection("users").document(usuario.getFirestoreDocId())
                .set(usuario).addOnSuccessListener(unused -> {

                    MyPreferences.setAccountSecure(true);

                    if (telaAtual == ACTIVITY_PROFILE) {
                        if (alertDialog!=null) alertDialog.dismiss();

                        ProfileActivity profileActivity = (ProfileActivity) context;
                        profileActivity.checarUser();
                    }

                    Toast.makeText(context, R.string.sucesso_ao_cadastrar_usuario, Toast.LENGTH_LONG).show();

                });

    }

    public void logarComGoogle(Intent data) {
        ProfileActivity profAct = (ProfileActivity) context;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialog = profAct.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(dialog);
        builder.setCancelable(false);

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        if (task.isSuccessful()) alertDialog = builder.show();

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(authCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                firestore.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        boolean temConta = false;
                                        if (documentSnapshot.exists()) temConta = true;
                                        else temConta = false;

                                        if (temConta) {// Caso tenha conta
                                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                            Database.setUsuario(usuario);

                                            // Recupera tarefas do servidor
                                            Database.deleteAllTarefas();
                                            for (Tarefa t : usuario.getTarefas()) {
                                                Database.addTarefa(t);
                                            }

                                            // Recupera tags do servidor
                                            Database.deleteAllTags();
                                            for (String s : usuario.getTags()) {
                                                Database.addTag(s);
                                            }

                                            Toast.makeText(context, R.string.sucesso_ao_logar, Toast.LENGTH_LONG).show();

                                            if (telaAtual == ACTIVITY_PROFILE) {
                                                if (alertDialog!=null) alertDialog.dismiss();

                                                ProfileActivity profileActivity = (ProfileActivity) context;
                                                profileActivity.checarUser();
                                            }

                                        } else {// Caso seja uma nova conta
                                            salvarNovaConta(account.getDisplayName(), account.getEmail(), "google");

                                        }
                                    }
                                });

                            } else {
                                if (alertDialog!=null) alertDialog.dismiss();

                                Toast.makeText(context, context.getString(R.string.erro_2p) + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logar(Credenciais c) {
        ProfileActivity profAct = (ProfileActivity) context;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialog = profAct.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(dialog);
        builder.setCancelable(false);

        alertDialog = builder.show();

        auth.signInWithEmailAndPassword(c.getEmail(), c.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, R.string.sucesso_ao_logar, Toast.LENGTH_LONG).show();

                            // Pegar dados do usuário remoto
                            DocumentReference documentReference = firestore.collection("users").document(auth.getUid());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    Database.setUsuario(usuario);

                                    // Recupera tarefas do servidor
                                    Database.deleteAllTarefas();
                                    for (Tarefa t : usuario.getTarefas()) {
                                        Database.addTarefa(t);
                                    }

                                    // Recupera tags do servidor
                                    Database.deleteAllTags();
                                    for (String s : usuario.getTags()) {
                                        Database.addTag(s);
                                    }

                                    if (telaAtual == ACTIVITY_PROFILE) {
                                        if (alertDialog!=null) alertDialog.dismiss();

                                        ProfileActivity profileActivity = (ProfileActivity) context;
                                        profileActivity.checarUser();
                                    }
                                }
                            });

                        } else {
                            if (alertDialog!=null) alertDialog.dismiss();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, context.getString(R.string.erro_2p) + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void criarConta(Credenciais c) {
        ProfileActivity profAct = (ProfileActivity) context;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialog = profAct.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(dialog);
        builder.setCancelable(false);

        alertDialog = builder.show();

        auth.createUserWithEmailAndPassword(c.getEmail(), c.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            salvarNovaConta(c.getNome(), c.getEmail(), "email");

                        } else {
                            if (alertDialog!=null) alertDialog.dismiss();

                            Toast.makeText(context, context.getString(R.string.erro_2p)+task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public FirebaseUser getFirebaseUser() {
        return auth.getCurrentUser();
    }

    public void deslogar() {
        if (listenerRegistration != null) removerListener();

        auth.signOut();
        MyPreferences.setAccountSecure(false);

        if (telaAtual == ACTIVITY_PROFILE) {
            ProfileActivity profileActivity = (ProfileActivity) context;
            profileActivity.checarUser();
        }

    }

    public void excluirConta() {
        String loginType = Database.getUsuario().getLoginType();

        if (telaAtual == ACTIVITY_PROFILE) {
            ProfileActivity profileActivity = (ProfileActivity) context;

            if (loginType.equals("email")) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(profileActivity);
                View dialog = profileActivity.getLayoutInflater().inflate(R.layout.dialog_confirm_exclude_account, null);
                builder.setView(dialog);

                AlertDialog alertDialog2 = builder.show();


                dialog.findViewById(R.id.btn_confirmar_exclusao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText senha = dialog.findViewById(R.id.txt_senha_dialog_exclude_account);

                        if (senha.getText().toString().equals("")) {
                            Toast.makeText(context, R.string.insira_sua_senha, Toast.LENGTH_LONG).show();
                            return;
                        }

                        AuthCredential authCredential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), senha.getText().toString());

                        auth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alertDialog2.dismiss();

                                    if (listenerRegistration != null) removerListener();
                                    MyPreferences.setAccountSecure(false);

                                    firestore.collection("users").document(auth.getUid())
                                            .delete();

                                    auth.getCurrentUser().delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, R.string.toast_conta_deletada, Toast.LENGTH_LONG).show();
                                                        profileActivity.checarUser();

                                                    }

                                                }

                                            });

                                } else {
                                    Toast.makeText(context, R.string.toast_verifique_sua_senha, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });
            } else if (loginType.equals("google")) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(profileActivity);
                builder.setTitle(R.string.excluir_conta);
                builder.setMessage(R.string.para_excluir_sua_conta_voce_precisa_relogar_nela_lembrando_que_essa_acao_nao_pode_ser_desfeita);

                builder.setNegativeButton(R.string.manter, null);

                builder.setPositiveButton(R.string.excluir_conta, (dialogInterface, i) -> {
                    Intent intent = googleSignInClient.getSignInIntent();
                    profileActivity.startActivityForResult(intent, 21);
                });

                builder.show();
            }

        }

    }

    public void excluirContaGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        GoogleSignInAccount account = null;
        try {
            account = task.getResult(ApiException.class);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);


        auth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listenerRegistration != null) removerListener();
                    MyPreferences.setAccountSecure(false);

                    firestore.collection("users").document(auth.getUid())
                            .delete();

                    auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(context, R.string.toast_conta_deletada, Toast.LENGTH_LONG).show();

                                if (telaAtual == ACTIVITY_PROFILE) {
                                    ProfileActivity profileActivity = (ProfileActivity) context;
                                    profileActivity.checarUser();
                                }

                            }

                        }

                    });

                } else {
                    Toast.makeText(context, R.string.toast_verifique_sua_senha, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void atualizarRemoto() {
        if (auth.getCurrentUser() != null) {
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
                                MyPreferences.setSincronizado(true);
                                MyPreferences.setAccountSecure(true);

                                atualizarLocal();
                            }
                        }
                    });
        }
    }

    public void atualizarLocal() {

        if (auth.getCurrentUser() != null) {
            listenerRegistration = firestore.collection("users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (auth.getCurrentUser() != null) {

                        if (!MyPreferences.isAccountSecure()) {
                            atualizarRemoto();
                            return;
                        }

                        Usuario usuarioRemoto = value.toObject(Usuario.class);

                        Database.setUsuario(usuarioRemoto);

                        // Recupera tarefas do servidor
                        Database.deleteAllTarefas();
                        for (Tarefa t : usuarioRemoto.getTarefas()) {
                            Database.addTarefa(t);
                        }

                        // Agendamento de tarefas
                        NotificationHelper notificationHelper = new NotificationHelper(context);
                        notificationHelper.agendarTarefas();

                        // Recupera tags do servidor
                        Database.deleteAllTags();
                        for (String s : usuarioRemoto.getTags()) {
                            Database.addTag(s);
                        }

                        if (telaAtual == ACTIVITY_MAIN) {
                            MainActivity mainActivity = (MainActivity) context;

                            mainActivity.recarregar();
                        }

                    }

                }
            });
        }

    }

    public void removerListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

}
