package modelos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class SistemaPregunta {

    private String pregunta;
    private String respuestaCorrecta;
    private List<String> opciones;
    private boolean activa;
    private Image fondoPregunta;
    private int opcionSeleccionada;

    // Lista de preguntas y respuestas
    private List<String[]> bancoPreguntas;

    public SistemaPregunta() {
        this.activa = false;
        this.opcionSeleccionada = -1;
        this.opciones = new ArrayList<>();
        this.bancoPreguntas = new ArrayList<>();
        cargarPreguntas();
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            fondoPregunta = ImageIO.read(getClass().getResource("/imagenes/fondos/fondo_pregunta.png"));
        } catch (IOException e) {
            System.out.println("Error al cargar imagen del fondo de pregunta: " + e.getMessage());
            fondoPregunta = null;
        }
    }

    private void cargarPreguntas() {
        // Matemáticas
        bancoPreguntas.add(new String[] { "¿Cuál es el valor de (5 + 3) × 2?", "16", "13", "16", "10", "15" });
        bancoPreguntas.add(new String[] { "¿Cuánto es el 25% de 200?", "50", "25", "75", "50", "100" });
        bancoPreguntas.add(new String[] { "Resuelve: 3x + 3 = 15. ¿Cuánto vale x?", "4", "5", "3", "4", "6" });

        // Español
        bancoPreguntas
                .add(new String[] { "¿Qué es un sustantivo?", "Palabra que nombra personas, animales, cosas o ideas",
                        "Palabra que nombra personas, animales, cosas o ideas",
                        "Palabra que describe una acción",
                        "Palabra que modifica al verbo",
                        "Palabra que une oraciones" });

        // Ciencias Naturales
        bancoPreguntas.add(new String[] { "¿Cuáles son los tres estados de la materia?", "Sólido, líquido y gaseoso",
                "Sólido, líquido y gaseoso",
                "Frío, tibio y caliente",
                "Sólido, plasma y gaseoso",
                "Mineral, animal y vegetal" });

        // Historia
        bancoPreguntas.add(new String[] { "¿En qué año se inició la Independencia de México?", "1810",
                "1810", "1821", "1910", "1917" });

        // Geografía
        bancoPreguntas.add(new String[] { "¿Qué continente es el más grande?", "Asia",
                "América", "Europa", "Asia", "África" });
    }

    public void generarPreguntaAleatoria() {
        if (bancoPreguntas.isEmpty()) {
            cargarPreguntas(); // Recargar si se agotaron
        }

        int indice = (int) (Math.random() * bancoPreguntas.size());
        String[] preguntaSeleccionada = bancoPreguntas.get(indice);

        this.pregunta = preguntaSeleccionada[0];
        this.respuestaCorrecta = preguntaSeleccionada[1];

        // Reinicia las opciones
        opciones.clear();
        for (int i = 2; i < preguntaSeleccionada.length; i++) {
            opciones.add(preguntaSeleccionada[i]);
        }

        // Mezclar opciones
        Collections.shuffle(opciones);

        this.activa = true;
        this.opcionSeleccionada = -1;

        // Eliminar la pregunta usada
        bancoPreguntas.remove(indice);
    }

    public void dibujar(Graphics g, int anchoVentana, int altoVentana) {
        if (!activa)
            return;

        // Dibujar fondo de pregunta
        int anchoFondo = 600;
        int altoFondo = 300;
        int xFondo = (anchoVentana - anchoFondo) / 2;
        int yFondo = (altoVentana - altoFondo) / 2;

        if (fondoPregunta != null) {
            g.drawImage(fondoPregunta, xFondo, yFondo, anchoFondo, altoFondo, null);
        } else {
            g.setColor(new Color(245, 222, 179));
            g.fillRect(xFondo, yFondo, anchoFondo, altoFondo);
            g.setColor(Color.RED);
            g.drawRect(xFondo, yFondo, anchoFondo, altoFondo);
        }
        // Fondo semitransparente negro
        g.setColor(new Color(225, 160, 87, 100));
        g.fillRect(100, 0, 600, altoVentana);

        // Dibujar pregunta
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // Dividir texto de pregunta para que se ajuste al ancho
        String[] palabras = pregunta.split(" ");
        String lineaActual = "";
        int y = yFondo + 60;
        int xTexto = xFondo + 50;
        int anchoTextoMax = anchoFondo - 100;

        for (String palabra : palabras) {
            if (g.getFontMetrics().stringWidth(lineaActual + palabra) < anchoTextoMax) {
                lineaActual += palabra + " ";
            } else {
                g.drawString(lineaActual, xTexto, y);
                y += 30;
                lineaActual = palabra + " ";
            }
        }
        g.drawString(lineaActual, xTexto, y);

        // Dibujar opciones
        g.setFont(new Font("Arial", Font.ITALIC, 18));
        y += 50;

        for (int i = 0; i < opciones.size(); i++) {
            if (i == opcionSeleccionada) {
                g.setColor(new Color(255, 215, 0)); // Dorado para seleccionado
                g.fillRect(xTexto - 30, y - 20, anchoTextoMax + 30, 30);
                g.setColor(Color.BLACK);
            }

            g.drawString((char) ('A' + i) + ") " + opciones.get(i), xTexto, y);
            y += 40;
        }

        // Instrucciones
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        g.drawString("Usa las teclas A, B, C, D para seleccionar y ENTER para confirmar",
                xFondo + 80, yFondo + altoFondo - 30);
    }

    public void seleccionarOpcion(int opcion) {
        if (activa && opcion >= 0 && opcion < opciones.size()) {
            opcionSeleccionada = opcion;
        }
    }

    public boolean confirmarRespuesta() {
        if (!activa || opcionSeleccionada == -1) {
            return false;
        }

        boolean esCorrecta = opciones.get(opcionSeleccionada).equals(respuestaCorrecta);
        activa = false;
        return esCorrecta;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }
}
