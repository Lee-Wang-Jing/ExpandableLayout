package com.wangjing.expandablelinearlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_demotext, btn_demoimageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_demotext = findViewById(R.id.btn_demotext);
        btn_demoimageview = findViewById(R.id.btn_demoimageview);

        btn_demotext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ExpandableTextViewDemoActivity.class));
            }
        });

        btn_demoimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ExpandableImageViewDemoActivity.class));
            }
        });
    }


}
