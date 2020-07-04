package com.zjy.trafficassist.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.zjy.trafficassist.R;
import com.zjy.trafficassist.ui.issue.PostRoadJam;

public class PostIssue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_issue);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onRoadJam(View v){
        startActivity(new Intent(PostIssue.this, PostRoadJam.class));
    }

    public void onRoadConstruction(View v){
        startActivity(new Intent(PostIssue.this, PostRoadJam.class));
    }

    public void onRoadClose(View v){
        startActivity(new Intent(PostIssue.this, PostRoadJam.class));
    }

    public void onRoadWater(View v){
        startActivity(new Intent(PostIssue.this, PostRoadJam.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
