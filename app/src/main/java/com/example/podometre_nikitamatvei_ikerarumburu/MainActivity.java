package com.example.podometre_nikitamatvei_ikerarumburu;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    private SensorManager sm;
    private Sensor s;
    private SensorEventListener event;
    // Variables per detectar passos
    private float lastMagnitude = 0;
    private long lastStepTime = 0;
    private static final float THRESHOLD = 11.5f;
    private static final long STEP_DELAY_MS = 300; // Temps minim entre passades


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
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        TextView textPassos = (TextView) findViewById(R.id.textView2);
        TextView textMetres = (TextView)findViewById(R.id.textView3);
        Button btnReiniciar = (Button)findViewById(R.id.button);
        textPassos.setText("Passos comptabilitzats: 0");
        textMetres.setText("Metres recorreguts: 0");

        if (s==null) {
            finish();
        }

        event = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent e) {

                float x = e.values[0];
                float y = e.values[1];
                float z = e.values[2];

                // Magnitud del vector aceleración
                float magnitud = (float) Math.sqrt(x*x + y*y + z*z);

                // Temps actual
                long now = System.currentTimeMillis();

                // Detectem una passada
                if (magnitud > THRESHOLD && lastMagnitude <= THRESHOLD) {
                    if (now - lastStepTime > STEP_DELAY_MS) {
                        numPassos++;
                        metresRecorreguts = numPassos; // ~1m por paso
                        lastStepTime = now;

                        textPassos.setText("Passos comptabilitzats: " + numPassos);
                        textMetres.setText("Metres recorreguts: " + metresRecorreguts);
                    }
                }

                // Guardem la última magnitud
                lastMagnitude = magnitud;
            }
        };

        sm.registerListener(event, s, SensorManager.SENSOR_DELAY_NORMAL);

        btnReiniciar.setOnClickListener(v -> {
            numPassos = 0;
            metresRecorreguts = 0;

            textPassos.setText("Passos comptabilitzats: " + numPassos);
            textMetres.setText("Metres recorreguts: " + metresRecorreguts);
        });


    }
}