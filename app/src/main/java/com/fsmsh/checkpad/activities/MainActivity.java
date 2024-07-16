package com.fsmsh.checkpad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.activities.edit.EditActivity;
import com.fsmsh.checkpad.ui.CategoryFragment;
import com.fsmsh.checkpad.ui.home.FragmentsIniciais;
import com.fsmsh.checkpad.ui.slideshow.SlideshowFragment;
import com.fsmsh.checkpad.util.Database;
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

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;
    private Database database;
    private int TELA_HOME_ATUAL = 0;
    private MenuItem menuItemHomeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL),false, false, false);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }else if (menuItem.getItemId() == R.id.nav_category) {
                    replaceFragment(new CategoryFragment(), false, false, false);
                }else if (menuItem.getItemId() == R.id.nav_slideshow) {
                    replaceFragment(new SlideshowFragment(), false, false, false);
                }

                drawerLayout.close();

                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL), false, false, false);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItemHomeAtual != menuItem) {
                    boolean leftToRight = false;
                    boolean highSpeed = false;
                    if (menuItem.getItemId() == R.id.item_naoIniciado) {
                        if (TELA_HOME_ATUAL == 2) highSpeed = true;
                        TELA_HOME_ATUAL = 0;
                        leftToRight = false;
                    } else if (menuItem.getItemId() == R.id.item_iniciado) {
                        if (TELA_HOME_ATUAL == 0) leftToRight = true;
                        else leftToRight = false;
                        TELA_HOME_ATUAL = 1;

                    } else if (menuItem.getItemId() == R.id.item_feitas) {
                        if (TELA_HOME_ATUAL == 0) highSpeed = true;
                        TELA_HOME_ATUAL = 2;
                        leftToRight = true;
                    }

                    replaceFragment(new FragmentsIniciais(TELA_HOME_ATUAL), true, leftToRight, highSpeed);
                }

                menuItemHomeAtual = menuItem;
                return true;
            }
        });
    }

    public void replaceFragment(Fragment fragment, boolean animar, boolean leftToRight, boolean highSpeed) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Anims
        if (animar) {
            if (!highSpeed) {
                if (leftToRight) transaction.setCustomAnimations(R.anim.enter_right_left, R.anim.exit_right_left);
                else transaction.setCustomAnimations(R.anim.enter_left_right, R.anim.exit_left_right);
            } else {
                if (leftToRight) transaction.setCustomAnimations(R.anim.enter_speed_right_left, R.anim.exit_speed_right_left);
                else transaction.setCustomAnimations(R.anim.enter_speed_left_right, R.anim.exit_speed_left_right);
            }
        }

        transaction.replace(R.id.nav_host_fragment_content_main, fragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}