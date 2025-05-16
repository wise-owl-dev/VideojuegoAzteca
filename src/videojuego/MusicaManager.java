package videojuego;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javazoom.jl.player.Player;

public class MusicaManager {

    private static MusicaManager instancia;

    // Rutas de los archivos de música
    private Map<String, String> rutasAudio;

    // Reproductor actual
    private Player reproductor;

    // Hilo para reproducción en segundo plano
    private Thread hiloReproduccion;

    // Estado de reproducción
    private boolean reproduciendo;
    private String musicaActual;

    private MusicaManager() {
        rutasAudio = new HashMap<>();

        // Configurar rutas de archivos de audio
        // La música de carga e inicio es la misma (menú)
        rutasAudio.put("menu", "/audio/menu.mp3");
        rutasAudio.put("juego", "/audio/juego.mp3");
        rutasAudio.put("batalla", "/audio/batalla.mp3");
        rutasAudio.put("victoria", "/audio/victoria.mp3");
        rutasAudio.put("derrota", "/audio/derrota.mp3");

        reproduciendo = false;
        musicaActual = "";
    }

    // Patrón Singleton para tener una única instancia
    public static MusicaManager getInstancia() {
        if (instancia == null) {
            instancia = new MusicaManager();
        }
        return instancia;
    }

    // Reproducir una pista de audio
    public void reproducir(String nombre) {
        // Si ya está reproduciendo esta pista, no hacer nada
        if (nombre.equals(musicaActual) && reproduciendo) {
            return;
        }

        // Detener cualquier reproducción actual
        detener();

        // Obtener la ruta del archivo
        String ruta = rutasAudio.get(nombre);
        if (ruta == null) {
            System.out.println("Error: No se encontró el archivo de audio: " + nombre);
            return;
        }

        // Iniciar reproducción en un nuevo hilo
        hiloReproduccion = new Thread(() -> {
            try {
                InputStream input = new BufferedInputStream(getClass().getResourceAsStream(ruta));
                reproductor = new Player(input);
                musicaActual = nombre;
                reproduciendo = true;

                System.out.println("Reproduciendo: " + nombre);
                reproductor.play();

                // Al terminar, reiniciar la reproducción en loop
                if (reproduciendo) {
                    reproducir(nombre);
                }
            } catch (Exception e) {
                System.out.println("Error al reproducir audio: " + e.getMessage());
            } finally {
                reproduciendo = false;
            }
        });

        hiloReproduccion.setDaemon(true); // El hilo se cerrará cuando finalice el programa
        hiloReproduccion.start();
    }

    // Pausar la reproducción
    public void pausar() {
        if (reproductor != null && reproduciendo) {
            try {
                // La librería JLayer no tiene método de pausa directamente,
                // así que guardamos el estado para después
                reproduciendo = false;
                cerrarReproductor();
            } catch (Exception e) {
                System.out.println("Error al pausar audio: " + e.getMessage());
            }
        }
    }

    // Detener completamente la reproducción
    public void detener() {
        if (reproductor != null) {
            reproduciendo = false;
            musicaActual = "";
            cerrarReproductor();
        }
    }

    // Cambiar a otra pista con transición suave
    public void cambiarA(String nombre) {
        // Si ya está reproduciendo esta pista, no hacer nada
        if (nombre.equals(musicaActual) && reproduciendo) {
            return;
        }

        // Simplemente detenemos y reproducimos la nueva pista
        // (podría implementarse un fadeout/fadein para transición más suave)
        detener();
        reproducir(nombre);
    }

    private void cerrarReproductor() {
        if (reproductor != null) {
            try {
                reproductor.close();
                reproductor = null;

                // Interrumpir el hilo
                if (hiloReproduccion != null && hiloReproduccion.isAlive()) {
                    hiloReproduccion.interrupt();
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar reproductor: " + e.getMessage());
            }
        }
    }

    // Para verificar si está reproduciendo
    public boolean estaReproduciendo() {
        return reproduciendo;
    }

    // Para verificar qué pista está sonando
    public String getMusicaActual() {
        return musicaActual;
    }
}
