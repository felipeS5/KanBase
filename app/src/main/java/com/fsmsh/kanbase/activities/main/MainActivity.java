package com.fsmsh.kanbase.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fsmsh.kanbase.R;
import com.fsmsh.kanbase.activities.about.AboutActivity;
import com.fsmsh.kanbase.activities.edit.EditActivity;
import com.fsmsh.kanbase.activities.profile.ProfileActivity;
import com.fsmsh.kanbase.activities.settings.SettingsActivity;
import com.fsmsh.kanbase.model.Tarefa;
import com.fsmsh.kanbase.activities.main.home.FragmentsIniciais;
import com.fsmsh.kanbase.activities.main.tags.TagsFragment;
import com.fsmsh.kanbase.util.AnimationRes;
import com.fsmsh.kanbase.util.Database;
import com.fsmsh.kanbase.util.DateUtilities;
import com.fsmsh.kanbase.util.FirebaseHelper;
import com.fsmsh.kanbase.util.Helper;
import com.fsmsh.kanbase.util.MyPreferences;
import com.fsmsh.kanbase.util.Sort;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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

    public static int FRAGMENT_HOME = 0;
    public static int FRAGMENT_TAGS = 1;

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    private Database database;
    public FirebaseHelper firebaseHelper;
    private MyPreferences myPreferences;
    private View view;

    private int telaHomeAtual;
    private int nFragmentAtual;
    private MenuItem menuItemHomeAtual;
    FragmentsIniciais homeAtual;
    TagsFragment tagsFragment;

    Fragment fragmentAtual;
    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.preConfigs(this);

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
                    replaceFragment(new FragmentsIniciais(telaHomeAtual, MainActivity.this), null);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    nFragmentAtual = FRAGMENT_HOME;

                } else if (menuItem.getItemId() == R.id.nav_category) {
                    replaceFragment(new TagsFragment(MainActivity.this), null);
                    bottomNavigationView.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);

                    nFragmentAtual = FRAGMENT_TAGS;

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

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItemHomeAtual != menuItem) {
                    AnimationRes animationRes = null;

                    if (menuItem.getItemId() == R.id.item_naoIniciado) {
                        if (telaHomeAtual == FragmentsIniciais.FINALIZADAS)
                            animationRes = new AnimationRes(false, true);
                        else animationRes = new AnimationRes(false, false);

                        telaHomeAtual = FragmentsIniciais.NOVAS;

                    } else if (menuItem.getItemId() == R.id.item_iniciado) {
                        if (telaHomeAtual == FragmentsIniciais.NOVAS)
                            animationRes = new AnimationRes(true, false);
                        else animationRes = new AnimationRes(false, false);

                        telaHomeAtual = FragmentsIniciais.INICIADAS;

                    } else if (menuItem.getItemId() == R.id.item_feitas) {
                        if (telaHomeAtual == FragmentsIniciais.NOVAS)
                            animationRes = new AnimationRes(true, true);
                        else animationRes = new AnimationRes(true, false);

                        telaHomeAtual = FragmentsIniciais.FINALIZADAS;

                    }

                    replaceFragment(new FragmentsIniciais(telaHomeAtual, MainActivity.this), animationRes);
                }

                menuItemHomeAtual = menuItem;
                return true;
            }
        });

        // Instancia o firebaseHelper
        firebaseHelper = new FirebaseHelper(this);

        // savedInstance
        if (savedInstanceState == null) { // Caso seja um novo estado de UI
            nFragmentAtual = FRAGMENT_HOME;
            telaHomeAtual = FragmentsIniciais.NOVAS;

            replaceFragment(new FragmentsIniciais(telaHomeAtual, MainActivity.this), null);
            navigationView.setCheckedItem(R.id.nav_home);

        } else { // Caso esteja resumindo um estado de UI
            nFragmentAtual = savedInstanceState.getInt("nFragmentAtual");
            if (nFragmentAtual == FRAGMENT_TAGS) {
                bottomNavigationView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }

            telaHomeAtual = savedInstanceState.getInt("telaHomeAtual");
            if (nFragmentAtual == FRAGMENT_HOME) {
                replaceFragment(new FragmentsIniciais(telaHomeAtual, MainActivity.this), null);

            } else if (nFragmentAtual == FRAGMENT_TAGS) {
                replaceFragment(new TagsFragment(MainActivity.this), null);

            }
        }

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

        // Destinado a atualizar o optionsMenu ????????????????????
        this.fragmentAtual = fragment;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

                    } else if (menuItem.getItemId() == R.id.menuClassifyLast) {
                        myPreferences.salvarPreferenciasClassify("creation", Sort.ORDEM_DECRESCENTE);

                    } else if (menuItem.getItemId() == R.id.menuClassifyMostImportants) {
                        myPreferences.salvarPreferenciasClassify("priority", Sort.ORDEM_CRESCENTE);

                    } else if (menuItem.getItemId() == R.id.menuClassifyLessImportants) {
                        myPreferences.salvarPreferenciasClassify("priority", Sort.ORDEM_DECRESCENTE);

                    }

                    if (nFragmentAtual == FRAGMENT_HOME) homeAtual.start();
                    else if (nFragmentAtual == FRAGMENT_TAGS) tagsFragment.start();

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

    public void recarregar() {
        if (isRunning) {
            if (homeAtual != null && fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
            if (tagsFragment != null && fragmentAtual instanceof TagsFragment) tagsFragment.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isRunning = true;

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

        isRunning = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("nFragmentAtual", nFragmentAtual);
        outState.putInt("telaHomeAtual", telaHomeAtual);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}