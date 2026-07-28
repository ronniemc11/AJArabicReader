package com.example.ajreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        Button playPause = (Button) findViewById(R.id.play_pause_button);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LiveActivity.this, R.string.live_not_available, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
