package com.fsmsh.checkpad.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.PopupMenu;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.model.Tarefa;
import com.fsmsh.checkpad.ui.home.FragmentsIniciais;
import com.fsmsh.checkpad.ui.slideshow.SlideshowFragment;
import com.fsmsh.checkpad.ui.tags.TagsFragment;
import com.fsmsh.checkpad.util.AnimationRes;
import com.fsmsh.checkpad.util.Database;
import com.fsmsh.checkpad.util.DateUtilities;
import com.fsmsh.checkpad.util.Sort;
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

import java.time.LocalDate;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    private Database database;
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
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        database = new Database(this);
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
                bottomNavigationView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);

                if (menuItem.getItemId() == R.id.nav_home) {
                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL, MainActivity.this),null);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }else if (menuItem.getItemId() == R.id.nav_category) {
                    replaceFragment(new com.fsmsh.checkpad.ui.tags.TagsFragment(), null);
                }else if (menuItem.getItemId() == R.id.nav_slideshow) {
                    replaceFragment(new SlideshowFragment(), null);
                }

                drawerLayout.close();

                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
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
                        if (TELA_HOME_ATUAL == FragmentsIniciais.FINALIZADAS) animationRes = new AnimationRes(false, true);
                        else animationRes = new AnimationRes(false, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.NOVAS;

                    } else if (menuItem.getItemId() == R.id.item_iniciado) {
                        if (TELA_HOME_ATUAL == FragmentsIniciais.NOVAS) animationRes = new AnimationRes(true, false);
                        else animationRes = new AnimationRes(false, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.INICIADAS;

                    } else if (menuItem.getItemId() == R.id.item_feitas) {
                        if (TELA_HOME_ATUAL == FragmentsIniciais.NOVAS) animationRes = new AnimationRes(true, true);
                        else animationRes = new AnimationRes(true, false);
                        TELA_HOME_ATUAL = FragmentsIniciais.FINALIZADAS;

                    }

                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL, MainActivity.this), animationRes);
                }

                menuItemHomeAtual = menuItem;
                return true;
            }
        });
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
        //if (hasMenu) onCreateOptionsMenu(menu);
        //hasMenu = true;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //this.menu = menu;
        //menu.clear();

        //if (fragmentAtual instanceof FragmentsIniciais) getMenuInflater().inflate(R.menu.main, menu);
        //else getMenuInflater().inflate(R.menu.main_tags, menu);

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
                        salvarPreferenciasClassify("creation", Sort.ORDEM_CRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyLast) {
                        salvarPreferenciasClassify("creation", Sort.ORDEM_DECRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyMostImportants) {
                        salvarPreferenciasClassify("priority", Sort.ORDEM_CRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    } else if (menuItem.getItemId() == R.id.menuClassifyLessImportants) {
                        salvarPreferenciasClassify("priority", Sort.ORDEM_DECRESCENTE);
                        if (fragmentAtual instanceof FragmentsIniciais) homeAtual.start();
                        else if (fragmentAtual instanceof TagsFragment) tagsFragment.start();

                    }

                    return true;
                }
            });

            popupMenu.show();
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

    private void salvarPreferenciasClassify(String classifyType, int ordem) {
        SharedPreferences preferences = getSharedPreferences("classify.pref", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString("classifyType", classifyType);
        prefEditor.putInt("ordem", ordem);

        prefEditor.apply();
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
        for (int progressoAtual = 0; progressoAtual <= 2; progressoAtual++) {
            List<Tarefa> tarefas = Database.getTarefas(progressoAtual);

            // loop que percorre a lista do estado atual
            for (Tarefa tarefa : tarefas) {
                if (!tarefa.getDateLimit().equals("")) {// verifica se tem limite
                    LocalDate dateLimit = DateUtilities.toLocalDate(tarefa.getDateLimit());

                    if (DateUtilities.isToday(dateLimit, 0)) {// aumenta a contagem de badges caso vença hoje
                        badges[progressoAtual]++;
                    }
                }
            }

        }

        return badges;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}