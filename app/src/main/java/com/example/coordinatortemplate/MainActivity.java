package com.example.coordinatortemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.coordinatortemplate.adapter.CoordinatorViewModule;
import com.google.android.material.appbar.AppBarLayout;

public class MainActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private TextView viewInCollapsingToolbarLayout;
    private TextView viewInToolBar;
    private NestedScrollView nestedScrollView;
    private CoordinatorViewModule coordinatorViewModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        coordinatorViewModule = new CoordinatorViewModule(this,appBarLayout,viewInCollapsingToolbarLayout, viewInToolBar, nestedScrollView);
    }

    private void init(){
        appBarLayout = findViewById(R.id.appBarLayout);
        viewInCollapsingToolbarLayout = findViewById(R.id.ViewInCollapsingToolbarLayout);
        viewInToolBar = findViewById(R.id.ViewInToolbar);
        nestedScrollView = findViewById(R.id.nestedscrollview);
    }
}