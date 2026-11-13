package com.example.reproductoralex; // Asegúrate de que coincide con tu paquete

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private SeekBar seekBar;
    private TextView tvActual, tvTotal;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean arrastrando = false;

    // Variables para el SoundPool
    private SoundPool soundPool;
    private int idClash, idMichael, idRoblox, idFahhh;
    private int sonidosCargados = 0; // Contador para saber cuándo están listos

    private final Runnable actualizarBarra = new Runnable() {
        @Override
        public void run() {
            if (!arrastrando && player != null) {
                long posicion = player.getCurrentPosition();
                seekBar.setProgress((int) posicion);
                tvActual.setText(formatearTiempo(posicion));
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- CONFIGURACIÓN REPRODUCTOR ---
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPausa = findViewById(R.id.btnPausa);
        seekBar = findViewById(R.id.seekBar);
        tvActual = findViewById(R.id.tvActual);
        tvTotal = findViewById(R.id.tvTotal);

        player = new ExoPlayer.Builder(this).build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.retu);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int estado) {
                if (estado == Player.STATE_READY) {
                    long duracion = player.getDuration();
                    seekBar.setMax((int) duracion);
                    tvTotal.setText(formatearTiempo(duracion));
                }
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (player.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                player.seekTo(0);
            }
            player.play();
        });

        btnPausa.setOnClickListener(v -> player.pause());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar barra) {
                arrastrando = true;
            }

            @Override
            public void onProgressChanged(SeekBar barra, int progreso, boolean delUsuario) {
                if (delUsuario) {
                    tvActual.setText(formatearTiempo(progreso));
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar barra) {
                player.seekTo(barra.getProgress());
                arrastrando = false;
            }
        });

        // --- CONFIGURACIÓN EFECTOS (SOUNDPOOL) ---
        AudioAttributes atributos = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(4) // Hasta 4 a la vez
                .setAudioAttributes(atributos)
                .build();

        // IMPORTANTE: Aquí usamos los nombres de archivo que tienes en tu captura
        idClash = soundPool.load(this, R.raw.royale, 1);
        idMichael = soundPool.load(this, R.raw.michael, 1);
        idRoblox = soundPool.load(this, R.raw.roblox, 1);
        idFahhh = soundPool.load(this, R.raw.fah, 1);

        // Listener para saber cuándo han terminado de cargar los 4 sonidos
        soundPool.setOnLoadCompleteListener((pool, sampleId, status) -> {
            if (status == 0) { // 0 significa que se cargó bien
                sonidosCargados++;
                if (sonidosCargados == 4) {
                    // Si ya están los 4, activamos los botones
                    findViewById(R.id.btnEf1).setEnabled(true);
                    findViewById(R.id.btnEf2).setEnabled(true);
                    findViewById(R.id.btnEf3).setEnabled(true);
                    findViewById(R.id.btnEf4).setEnabled(true);
                }
            }
        });

        // Configurar los botones para que suenen al pulsarlos
        findViewById(R.id.btnEf1).setOnClickListener(v -> soundPool.play(idClash, 1f, 1f, 1, 0, 1f));
        findViewById(R.id.btnEf2).setOnClickListener(v -> soundPool.play(idMichael, 1f, 1f, 1, 0, 1f));
        findViewById(R.id.btnEf3).setOnClickListener(v -> soundPool.play(idRoblox, 1f, 1f, 1, 0, 1f));
        findViewById(R.id.btnEf4).setOnClickListener(v -> soundPool.play(idFahhh, 1f, 1f, 1, 0, 1f));
    }

    private String formatearTiempo(long ms) {
        long segundos = ms / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(actualizarBarra);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null && player.isPlaying()) {
            player.pause();
        }
        handler.removeCallbacks(actualizarBarra);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        // Liberar también el SoundPool
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}