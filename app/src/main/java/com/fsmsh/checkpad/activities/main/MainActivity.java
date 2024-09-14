package com.fsmsh.checkpad.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.about.AboutActivity;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.activities.profile.ProfileActivity;
import com.fsmsh.checkpad.activities.settings.SettingsActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.activities.main.home.FragmentsIniciais;
import com.fsmsh.checkpad.activities.main.tags.TagsFragment;
import com.fsmsh.checkpad.util.AnimationRes;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.DateUtilities;
import com.fsmsh.checkpad.util.FirebaseHelper;
import com.fsmsh.checkpad.util.Helper;
import com.fsmsh.checkpad.util.MyPreferences;
import com.fsmsh.checkpad.util.NotificationHelper;
import com.fsmsh.checkpad.util.Sort;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    private Database database;
    private FirebaseHelper firebaseHelper;
    private MyPreferences myPreferences;
    private View view;

    private int TELA_HOME_ATUAL = FragmentsIniciais.NOVAS;
    private MenuItem menuItemHomeAtual;
    FragmentsIniciais homeAtual;
    TagsFragment tagsFragment;

    Fragment fragmentAtual;
    Menu menu;
    boolean hasMenu = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preConfigs();

        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        database = new Database(this);
        myPreferences = new MyPreferences(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("isNovo", true);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_home) {
                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL, MainActivity.this), null);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                } else if (menuItem.getItemId() == R.id.nav_category) {
                    replaceFragment(new TagsFragment(MainActivity.this), null);
                    bottomNavigationView.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);

                } else if (menuItem.getItemId() == R.id.nav_config) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);

                } else if (menuItem.getItemId() == R.id.nav_about) {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);

                }

                drawerLayout.close();

                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL, MainActivity.this), null);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItemHomeAtual != menuItem) {
                    AnimationRes animationRes = null;

                    if (menuItem.getItemId() == R.id.item_naoIniciado) {
                        if (TELA_HOME_ATUAL == FragmentsIniciais.FINALIZADAS)
                            animationRes = new AnimationRes(false, true);
                        else animationRes = new AnimationRes(false, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.NOVAS;

                    } else if (menuItem.getItemId() == R.id.item_iniciado) {
                        if (TELA_HOME_ATUAL == FragmentsIniciais.NOVAS)
                            animationRes = new AnimationRes(true, false);
                        else animationRes = new AnimationRes(false, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.INICIADAS;

                    } else if (menuItem.getItemId() == R.id.item_feitas) {
                        if (TELA_HOME_ATUAL == FragmentsIniciais.NOVAS)
                            animationRes = new AnimationRes(true, true);
                        else animationRes = new AnimationRes(true, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.FINALIZADAS;

                    }

                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL, MainActivity.this), animationRes);
                }

                menuItemHomeAtual = menuItem;
                return true;
            }
        });

        // Instancia o firebaseHelper
        firebaseHelper = new FirebaseHelper(this);

    }

    public void replaceFragment(Fragment fragment, AnimationRes animationRes) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Anims
        if (animationRes != null) {
            transaction.setCustomAnimations(animationRes.getAnimResEnter(), animationRes.getAnimResExit());
        }

        transaction.replace(R.id.nav_host_fragment_content_main, fragment);
        transaction.commit();

        if (fragment instanceof FragmentsIniciais) homeAtual = (FragmentsIniciais) fragment;
        else if (fragment instanceof TagsFragment) tagsFragment = (TagsFragment) fragment;

        // Destinado a atualizar o optionsMenu
        this.fragmentAtual = fragment;
        if (hasMenu) onCreateOptionsMenu(menu);
        hasMenu = true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.clear();

        if (fragmentAtual instanceof FragmentsIniciais || fragmentAtual instanceof TagsFragment)
            getMenuInflater().inflate(R.menu.main, menu);
        else
            getMenuInflater().inflate(R.menu.main_tags, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.test) {
            //Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);

            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), findViewById(R.id.test));
            popupMenu.inflate(R.menu.filter_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menuClassifyFirs) {
                        myPreferences.salvarPreferenciasClassify("creation", Sort.ORDEM_CRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyLast) {
                        myPreferences.salvarPreferenciasClassify("creation", Sort.ORDEM_DECRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyMostImportants) {
                        myPreferences.salvarPreferenciasClassify("priority", Sort.ORDEM_CRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyLessImportants) {
                        myPreferences.salvarPreferenciasClassify("priority", Sort.ORDEM_DECRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    }

                    return true;
                }
            });
            popupMenu.show();
            return true;

        } else if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setBadges() {
        int[] badges = getBadges();

        BadgeDrawable badgeNovas = bottomNavigationView.getOrCreateBadge(R.id.item_naoIniciado);
        BadgeDrawable badgeIniciadas = bottomNavigationView.getOrCreateBadge(R.id.item_iniciado);

        if (badges[0] > 0) {
            //badgeDrawable = BadgeDrawable.create(this);
            badgeNovas.setVisible(true);
            badgeNovas.setNumber(badges[0]);
            //BadgeUtils.attachBadgeDrawable(badgeDrawable, findViewById(R.id.item_iniciado));
        } else {
            badgeNovas.setVisible(false);
        }

        if (badges[1] > 0) {
            badgeIniciadas.setVisible(true);
            badgeIniciadas.setNumber(badges[1]);

        } else {
            badgeIniciadas.setVisible(false);
        }
        // As badges não serão exibidas na aba de finalizadas... (já foram finalizadas)

    }

    public int[] getBadges() {
        int[] badges = new int[3];

        // loop que percorre os 3 estados possíveis
        for (int progressPercorrido = 0; progressPercorrido <= 2; progressPercorrido++) {
            List<Tarefa> tarefas = Database.getTarefas(progressPercorrido);

            // loop que percorre a lista do estado atual
            for (Tarefa tarefa : tarefas) {
                if (DateUtilities.isAtrazada(tarefa) || DateUtilities.isVencendoHoje(tarefa)) {
                    badges[progressPercorrido]++; // aumenta a contagem de badges caso esteja atrazada vença hoje
                }
            }

        }

        return badges;
    }

    public void adjustHeader() {
        View header = navigationView.getHeaderView(0); // peguei esse trem no stackOverflow

        TextView noProblem = header.findViewById(R.id.header_congrats);
        TextView lblWelcome = header.findViewById(R.id.header_welcome);
        TextView lblAtrazada = header.findViewById(R.id.header_tarefas_atrazadas);
        TextView lblVencendo = header.findViewById(R.id.header_tarefas_vencendo);

        // Retorna ao estado inicial
        noProblem.setVisibility(View.VISIBLE);
        lblWelcome.setVisibility(View.GONE);
        lblAtrazada.setVisibility(View.GONE);
        lblVencendo.setVisibility(View.GONE);

        List<Tarefa> tarefas = Database.getTarefas(Database.PROGRESS_TODOS);
        int tarefasAtrazadas = 0;
        int tarefasVencendo = 0;

        for (Tarefa t : tarefas) {
            // Atarefa poderá ser encaixada nos 2 casos
            if (DateUtilities.isAtrazada(t) && t.getProgresso() != FragmentsIniciais.FINALIZADAS)
                tarefasAtrazadas++;
            if (DateUtilities.isVencendoHoje(t) && t.getProgresso() != FragmentsIniciais.FINALIZADAS)
                tarefasVencendo++;
        }

        if (tarefasAtrazadas > 0 || tarefasVencendo > 0) {
            noProblem.setVisibility(View.GONE);
            lblWelcome.setVisibility(View.VISIBLE);

            String saudacoes = DateUtilities.getSaudacoes(getApplicationContext());
            if (firebaseHelper.getFirebaseUser() != null)
                saudacoes += " " + Database.getUsuario().getNome();
            lblWelcome.setText(saudacoes + getString(R.string.v_voce_tem_2p));

            if (tarefasAtrazadas > 0) {
                lblAtrazada.setVisibility(View.VISIBLE);
                String texto;

                if (tarefasAtrazadas == 1)
                    texto = tarefasAtrazadas + getString(R.string.sp_tarefa_atrazada);
                else texto = tarefasAtrazadas + getString(R.string.sp_tarefas_atrazadas);

                lblAtrazada.setText(texto);
            }

            if (tarefasVencendo > 0) {
                lblVencendo.setVisibility(View.VISIBLE);
                String texto;

                if (tarefasVencendo == 1)
                    texto = tarefasVencendo + getString(R.string.sp_tarefa_vencendo);
                else texto = tarefasVencendo + getString(R.string.sp_tarefas_vencendo);

                lblVencendo.setText(texto);
            }
        }

    }


    // Ajustes de systema
    private void preConfigs() {
        new MyPreferences(getApplicationContext());

        // Idioma
        String idiomaAtual = MyPreferences.getIdioma();
        Helper.setLocale(MainActivity.this, idiomaAtual);

        // Tema
        int tema = MyPreferences.getTema();
        AppCompatDelegate.setDefaultNightMode(tema);

        // Resumo diário
        if (MyPreferences.isDailySumaryActive()) {
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.configurarChannel();
            notificationHelper.agendarNotificacaoDiaria();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verifica se deve restartar (ao mudar idioma)
        if (MyPreferences.isPendingRestart()) {
            MyPreferences.setPendingRestart(false);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }


        // Verifica se falta fazer upload de alguma tarefa
        if (firebaseHelper.getFirebaseUser() != null) {
            if (!MyPreferences.isSincronizado()) {
                firebaseHelper.atualizarRemoto();
            } else {
                firebaseHelper.atualizarLocal();
            }
        }

        adjustHeader();

    }

    @Override
    protected void onPause() {
        super.onPause();

        firebaseHelper.removerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}