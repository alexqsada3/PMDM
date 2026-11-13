package com.example.reproductoralex; // Asegúrate de que esta línea es igual a la tuya

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private Button btnPlay, btnPausa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPausa = findViewById(R.id.btnPausa);

        // Crear ExoPlayer
        player = new ExoPlayer.Builder(this).build();

        // Cargar la canción (asegúrate de que se llama retu.mp3 en tu carpeta raw)
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.retu);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();

        // Listener del botón Play
        btnPlay.setOnClickListener(v -> {
            // Si la canción ya había terminado, volver al principio
            if (player.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                player.seekTo(0);
            }
            player.play();
        });

        // Listener del botón Pausa
        btnPausa.setOnClickListener(v -> player.pause());
    }
}