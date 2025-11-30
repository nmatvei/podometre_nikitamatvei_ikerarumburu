package com.example.podometre_nikitamatvei_ikerarumburu;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Variables per contar passos i metres
    double metresRecorreguts = 0;
    int numPassos = 0;
    int passosObjectiusEnter = 0;
    private SensorManager sm;
    private Sensor s;
    private SensorEventListener event;

    // Variables per detectar passos
    private float lastMagnitude = 0;
    private long lastStepTime = 0;
    private static final float THRESHOLD = 11.5f;
    // Temps minim entre passades
    private static final long STEP_DELAY_MS = 500;

    // Variables per l'animació
    private ImageView walkingImage;
    private AnimationDrawable walkingAnimation;


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
        EditText objectiuPassos = findViewById(R.id.objectiuPassos);
        textPassos.setText("Passos comptabilitzats: 0");
        textMetres.setText("Metres recorreguts: 0");

        walkingImage = findViewById(R.id.imageView);
        walkingAnimation = (AnimationDrawable) walkingImage.getDrawable();

        // Si el sensor no existeix tanquem la app
        if (s==null) {
            finish();
        }

        // Afegim un listener al sensor
        event = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}

            /**
             * Métode que es crida quan hi ha canvis en el sensor.
             *
             * @param e the {@link android.hardware.SensorEvent SensorEvent}.
             */
            @Override
            public void onSensorChanged(SensorEvent e) {

                // Valors per x, y i z
                float x = e.values[0];
                float y = e.values[1];
                float z = e.values[2];

                // Magnitud d'aceleració
                float magnitud = (float) Math.sqrt(x*x + y*y + z*z);

                // Obtenim el temps actual
                long now = System.currentTimeMillis();

                // Detectem una passada
                if (magnitud > THRESHOLD && lastMagnitude <= THRESHOLD) {
                    // Si el temps actual menys el temps de la ultima passada es major al diley entre passades
                    if (now - lastStepTime > STEP_DELAY_MS) {
                        if (!walkingAnimation.isRunning()) {
                            walkingAnimation.start();
                        }

                        // Sumem una passada
                        numPassos++;
                        metresRecorreguts = numPassos * 0.75;

                        // Actualitzem el temps
                        lastStepTime = now;

                        // Actualitzem els textos de passos i metres
                        textPassos.setText("Passos comptabilitzats: " + numPassos);
                        textMetres.setText("Metres recorreguts: " + metresRecorreguts);

                        // Llegim els passos objectius
                        String objetivoTexto = objectiuPassos.getText().toString();
                        if (!objetivoTexto.isEmpty()) {
                            try {
                                passosObjectiusEnter = Integer.parseInt(objetivoTexto);
                            } catch (NumberFormatException ex) {
                                passosObjectiusEnter = 0;
                            }
                        }

                        // Comprovar si s'ha arribat a l'objetiu
                        if (numPassos == passosObjectiusEnter && passosObjectiusEnter > 0) {
                            Toast.makeText(getApplicationContext(), "¡Has arribat als passos objectius!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // Si el temps és menor a 1s, detenim l'animació
                if (now - lastStepTime > 1000) {
                    if (walkingAnimation.isRunning()) {
                        walkingAnimation.stop();
                    }
                }

                // Guardem la última magnitud
                lastMagnitude = magnitud;
            }
        };

        sm.registerListener(event, s, SensorManager.SENSOR_DELAY_NORMAL);

        // Afegir un listener pel botó de reiniciar
        btnReiniciar.setOnClickListener(v -> {
            numPassos = 0;
            metresRecorreguts = 0;
            objectiuPassos.setText("");
            textPassos.setText("Passos comptabilitzats: " + numPassos);
            textMetres.setText("Metres recorreguts: " + metresRecorreguts);
            if (walkingAnimation.isRunning()) {
                walkingAnimation.stop();
            }
        });


    }
}