package com.example.reproductoralex; // Asegúrate de que coincide con tu paquete

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
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
    private Button btnPlay, btnPausa;
    private SeekBar seekBar;
    private TextView tvActual, tvTotal;

    // El Handler se encarga de repetir la actualización cada medio segundo
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean arrastrando = false; // Para saber si el usuario está moviendo la barra

    private final Runnable actualizarBarra = new Runnable() {
        @Override
        public void run() {
            // Si el usuario no está arrastrando la barra y el player existe...
            if (!arrastrando && player != null) {
                long posicion = player.getCurrentPosition();
                seekBar.setProgress((int) posicion);
                tvActual.setText(formatearTiempo(posicion));
            }
            // Se vuelve a llamar a sí mismo en 500 milisegundos
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPausa = findViewById(R.id.btnPausa);
        seekBar = findViewById(R.id.seekBar);
        tvActual = findViewById(R.id.tvActual);
        tvTotal = findViewById(R.id.tvTotal);

        player = new ExoPlayer.Builder(this).build();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.retu);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();

        // Listener para saber cuándo la canción está lista y coger su duración
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

        // Listener para cuando el usuario toca y mueve la barra
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar barra) {
                arrastrando = true; // El usuario ha cogido la barra
            }

            @Override
            public void onProgressChanged(SeekBar barra, int progreso, boolean delUsuario) {
                if (delUsuario) {
                    tvActual.setText(formatearTiempo(progreso));
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar barra) {
                player.seekTo(barra.getProgress()); // Salta a ese punto
                arrastrando = false; // El usuario ha soltado la barra
            }
        });
    }

    // Método para formatear los milisegundos a mm:ss
    private String formatearTiempo(long ms) {
        long segundos = ms / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", segundos / 60, segundos % 60);
    }
}