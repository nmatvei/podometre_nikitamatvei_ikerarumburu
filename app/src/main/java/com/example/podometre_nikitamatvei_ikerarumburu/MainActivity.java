package com.example.podometre_nikitamatvei_ikerarumburu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    int metresRecorreguts = 0;
    int numPassos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textPassos = (TextView) findViewById(R.id.textView2);
        TextView textMetres = (TextView)findViewById(R.id.textView3);
        Button btnReiniciar = (Button)findViewById(R.id.button);

        btnReiniciar.setOnClickListener(v -> {
            numPassos = 0;
            metresRecorreguts = 0;

            textPassos.setText("Passos comptabilitzats: " + numPassos);
            textMetres.setText("Metres recorreguts: " + metresRecorreguts);
        });


    }
}