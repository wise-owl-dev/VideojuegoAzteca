package videojuego;

import javax.swing.*;
import javax.swing.text.html.parser.Element;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import modelos.*;

public class GuerreroAzteca extends JPanel implements ActionListener, KeyListener {

    private static final int ANCHO = 800;
    private static final int ALTO = 300;
    private static final int SUELO = 230; // Ajuste de la posición del suelo
    private static final int DELAY = 15;
    private boolean siguienteEsArbol = true;
    private static final int DISTANCIA_MINIMA_ELEMENTOS = 350;
    private EfectoFuego efectoFuegoJefe;
    private EfectoFuego efectoFuegoGuerrero;
    private boolean mostrandoEfectoFuego;

    private Guerrero guerrero;
    private ArrayList<Obstaculo> obstaculos;
    private ArrayList<Aguila> aguilas;
    private ArrayList<Coleccionable> coleccionables;
    private ArrayList<ElementoAmbiente> elementosAmbiente;
    private Timer timer;
    private int velocidad;
    private int puntuacion;
    private int oroRecogido;
    private int frutasRecogidas;
    private boolean juegoActivo;
    private Random random;
    private Image fondoImagen;
    private Image sueloImagen;
    private int posicionFondo;

    // Nuevas variables para el jefe final
    private JefeFinal jefeFinal;
    private boolean modoBatalla;
    private Image fondoBatalla;
    private SistemaPregunta sistemaPregunta;
    private boolean esperandoAtaque;

    // Imágenes de ataque del guerrero
    private Image[] framesAtaque;
    private int frameAtaqueActual;
    private boolean atacando;
    private long ultimoCambioFrameAtaque;
    private static final long DURACION_FRAME_ATAQUE = 100;

    // Vidas del jugador
    private int vidasJugador;
    private Image imagenCorazon;

    public GuerreroAzteca() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(new Color(255, 220, 180));
        setFocusable(true);
        addKeyListener(this);
        cargarImagenes();
        iniciarJuego();
    }

    private void cargarImagenes() {
        try {
            // Cargar imagen de fondo
            fondoImagen = ImageIO.read(new File("src/imagenes/fondo.png"));
            // Cargar imagen de suelo
            sueloImagen = ImageIO.read(new File("src/imagenes/suelo.png"));

            // Cargar nuevas imágenes
            fondoBatalla = ImageIO.read(new File("src/imagenes/fondos/fondo_batalla.png"));
            imagenCorazon = ImageIO.read(new File("src/imagenes/ui/corazon.png"));

            // Cargar frames de ataque
            framesAtaque = new Image[8];
            for (int i = 0; i < 8; i++) {
                framesAtaque[i] = ImageIO.read(new File("src/imagenes/guerrero/ataque" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            System.out.println("Error al cargar imágenes de fondo: " + e.getMessage());
        }
    }

    private void iniciarJuego() {
        guerrero = new Guerrero(80, SUELO);
        obstaculos = new ArrayList<>();
        coleccionables = new ArrayList<>();
        elementosAmbiente = new ArrayList<>();
        aguilas = new ArrayList<>(); // Inicializar la lista de águilas

        // No generamos águilas iniciales, dejaremos que aparezcan naturalmente
        velocidad = 5;
        puntuacion = 0;
        oroRecogido = 0;
        frutasRecogidas = 0;
        juegoActivo = true;
        random = new Random();
        posicionFondo = 0;

        // Nuevas inicializaciones
        jefeFinal = null;
        modoBatalla = false;
        sistemaPregunta = new SistemaPregunta();
        vidasJugador = 5;
        esperandoAtaque = false;
        atacando = false;
        frameAtaqueActual = 0;

        efectoFuegoJefe = new EfectoFuego();
        efectoFuegoGuerrero = new EfectoFuego();
        mostrandoEfectoFuego = false;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void generarColeccionable() {
        // Probabilidad reducida del 15% al 5% para que aparezcan menos coleccionables
        if (random.nextInt(100) < 5) { // 5% de probabilidad
            int tipo = random.nextInt(2); // 0: oro, 1: fruta
            int subtipo = 0;

            if (tipo == 0) { // Si es oro, elegir uno de los 10 tipos
                subtipo = random.nextInt(10) + 1; // oro1, oro2, ..., oro10
            }

            // Posición Y más variable para que no estén todos a la misma altura
            int y = SUELO - random.nextInt(40) - 40; // Entre 40 y 80 píxeles por encima del suelo
            coleccionables.add(new Coleccionable(ANCHO, y, tipo, subtipo));
        }
    }

    private void generarAguila() {
        // Limitar el número máximo de águilas en pantalla
        int maxAguilas = 2; // Máximo 2 águilas simultáneas

        // Verificar si ya tenemos suficientes águilas
        if (aguilas.size() >= maxAguilas) {
            return;
        }

        // Reducir la frecuencia de aparición a 1%
        if (random.nextInt(1000) < 10) { // 1% de probabilidad
            // Altura variable para el águila (cielo)
            int alturaAguila = random.nextInt(60) + 20; // Entre 20 y 80 píxeles de altura

            // Las águilas siempre vienen desde fuera de la pantalla a la derecha
            float posicionX = ANCHO + random.nextInt(50);

            aguilas.add(new Aguila(posicionX, alturaAguila));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujar(g);
    }

    private void dibujar(Graphics g) {
        if (modoBatalla) {
            dibujarEscenarioBatalla(g);
        } else {
            // Dibujo normal del juego
            dibujarFondo(g);

            // Dibujar águilas (como parte del fondo/cielo)
            for (Aguila aguila : aguilas) {
                aguila.dibujar(g);
            }

            // Dibujar elementos de ambiente (solo árboles, decorativos)
            for (ElementoAmbiente elemento : elementosAmbiente) {
                elemento.dibujar(g);
            }

            // Dibujar suelo
            if (sueloImagen != null) {
                for (int x = -posicionFondo % 64; x < ANCHO; x += 64) {
                    g.drawImage(sueloImagen, x, SUELO, null);
                }
            } else {
                g.setColor(new Color(120, 80, 40));
                g.fillRect(0, SUELO + 20, ANCHO, 2);
            }

            // Dibujar obstáculos sin resaltado
            for (Obstaculo obs : obstaculos) {
                obs.dibujar(g);
            }

            // Dibujar coleccionables
            for (Coleccionable coleccionable : coleccionables) {
                coleccionable.dibujar(g);
            }

            // Dibujar guerrero
            guerrero.dibujar(g);

            // Dibujar interfaz
            dibujarInterfaz(g);
        }

        // Dibujar sistema de preguntas por encima de todo
        if (sistemaPregunta.estaActiva()) {
            sistemaPregunta.dibujar(g, ANCHO, ALTO);
        }

        // Dibujar pantalla de game over
        if (!juegoActivo) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", 320, 130);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Puntuación: " + puntuacion, 340, 160);
            g.drawString("Oro recogido: " + oroRecogido, 330, 185);
            g.drawString("Frutas recogidas: " + frutasRecogidas, 310, 210);
            g.drawString("Presiona Espacio para reiniciar", 270, 245);
        }
    }

    private void dibujarEscenarioBatalla(Graphics g) {
        // Dibujar fondo de batalla
        if (fondoBatalla != null) {
            g.drawImage(fondoBatalla, 0, 0, ANCHO, ALTO, null);
        } else {
            g.setColor(new Color(100, 50, 50));
            g.fillRect(0, 0, ANCHO, ALTO);
        }

        // Dibujar suelo de la batalla
        if (sueloImagen != null) {
            for (int x = 0; x < ANCHO; x += 64) {
                g.drawImage(sueloImagen, x, SUELO, null);
            }
        }

        // Dibujar elementos de ambiente que quedaron
        for (ElementoAmbiente elemento : elementosAmbiente) {
            elemento.dibujar(g);
        }

        // Dibujar jefe final
        if (jefeFinal != null) {
            jefeFinal.dibujar(g);

            // Dibujar vidas del jefe
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Jefe: ", 550, 40);

            /*
             * for (int i = 0; i < jefeFinal.getVidas(); i++) {
             * g.setColor(Color.RED);
             * g.fillRect(700 + i * 25, 20, 20, 20);
             * }
             */

            // Dibujar vidas del jugador
            for (int i = 0; i < jefeFinal.getVidas(); i++) {
                if (imagenCorazon != null) {
                    g.drawImage(imagenCorazon, 600 + i * 40, 20, 30, 30, null);
                } else {
                    g.setColor(Color.RED);
                    g.fillOval(600 + i * 40, 20, 30, 30);
                }
            }
        }

        // Dibujar guerrero (normal o atacando)
        if (atacando && framesAtaque != null && frameAtaqueActual < framesAtaque.length) {
            g.drawImage(framesAtaque[frameAtaqueActual], guerrero.getX(), guerrero.getY() - 51, null);
        } else {
            guerrero.dibujar(g);
        }

        // Dibujar vidas del jugador
        for (int i = 0; i < vidasJugador; i++) {
            if (imagenCorazon != null) {
                g.drawImage(imagenCorazon, 20 + i * 40, 20, 30, 30, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(20 + i * 40, 20, 30, 30);
            }
        }

        // Dibujar efectos de fuego
        if (efectoFuegoJefe != null && efectoFuegoJefe.estaActivo()) {
            efectoFuegoJefe.dibujar(g);
        }

        if (efectoFuegoGuerrero != null && efectoFuegoGuerrero.estaActivo()) {
            efectoFuegoGuerrero.dibujar(g);
        }

        // Dibujar puntuación
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Puntuación: " + puntuacion, 20, 80);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (sistemaPregunta.estaActiva()) {
            // Controles para el sistema de preguntas
            switch (key) {
                case KeyEvent.VK_A:
                    sistemaPregunta.seleccionarOpcion(0);
                    break;
                case KeyEvent.VK_B:
                    sistemaPregunta.seleccionarOpcion(1);
                    break;
                case KeyEvent.VK_C:
                    sistemaPregunta.seleccionarOpcion(2);
                    break;
                case KeyEvent.VK_D:
                    sistemaPregunta.seleccionarOpcion(3);
                    break;
                case KeyEvent.VK_ENTER:
                    boolean respuestaCorrecta = sistemaPregunta.confirmarRespuesta();
                    procesarRespuesta(respuestaCorrecta);
                    break;
            }
        } else {
            // Controles normales del juego
            if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) {
                if (juegoActivo) {
                    if (!guerrero.estaSaltando()) {
                        guerrero.saltar();
                    }
                } else {
                    reiniciarJuego();
                }
            }

            // Atacar en modo batalla
            if (modoBatalla && (key == KeyEvent.VK_X || key == KeyEvent.VK_CONTROL)) {
                if (!atacando && !sistemaPregunta.estaActiva()) {
                    atacando = true;
                    frameAtaqueActual = 0;
                    ultimoCambioFrameAtaque = System.currentTimeMillis();
                    esperandoAtaque = true;
                }
            }
        }
    }

    private void procesarRespuesta(boolean esCorrecta) {
        if (modoBatalla && jefeFinal != null) {
            mostrandoEfectoFuego = true;

            if (esCorrecta) {
                // Respuesta correcta: Mostrar fuego en el jefe
                // Posicionar el fuego en el centro del jefe
                int xFuego = jefeFinal.getX() + 100; // Ajusta según el tamaño del jefe
                int yFuego = jefeFinal.getY() + 80; // Ajusta según el tamaño del jefe
                efectoFuegoJefe.iniciar(xFuego, yFuego);

                // Quitar vida al jefe (después de la animación en actionPerformed)
                new Timer(800, e -> {
                    boolean jefeVencido = jefeFinal.perdioVida();
                    puntuacion += 500;

                    if (jefeVencido) {
                        // El jugador derrotó al jefe
                        jefeFinal = null;
                        puntuacion += 2000;
                        modoBatalla = false;
                    }
                    ((Timer) e.getSource()).stop();
                }).start();
            } else {
                // Respuesta incorrecta: Mostrar fuego en el guerrero
                int xFuego = guerrero.getX() + 25; // Ajusta según el tamaño del guerrero
                int yFuego = guerrero.getY() - 50; // Ajusta para que el fuego aparezca cerca del guerrero
                efectoFuegoGuerrero.iniciar(xFuego, yFuego);

                // Quitar vida al jugador (después de la animación)
                new Timer(800, e -> {
                    vidasJugador--;

                    if (vidasJugador <= 0) {
                        // Game over
                        juegoActivo = false;
                        timer.stop();
                    }
                    ((Timer) e.getSource()).stop();
                }).start();
            }
        }
    }

    private void destacarObstaculo(Graphics g, Obstaculo obs) {
        // Destacar visualmente los obstáculos con un contorno semitransparente
        Graphics2D g2d = (Graphics2D) g;

        // Guardar configuraciones originales
        Composite originalComposite = g2d.getComposite();
        Stroke originalStroke = g2d.getStroke();

        // Establecer transparencia
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2d.setComposite(alphaComposite);

        // Establecer un trazo más grueso para el contorno
        g2d.setStroke(new BasicStroke(3.0f));

        // Obtener la hitbox del obstáculo
        Rectangle hitbox = obs.getHitbox();

        // Dibujar un contorno alrededor del obstáculo
        g2d.setColor(Color.RED);
        g2d.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

        // Restaurar configuraciones originales
        g2d.setComposite(originalComposite);
        g2d.setStroke(originalStroke);
    }

    private void dibujarInterfaz(Graphics g) {
        // Panel semitransparente
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(10, 10, 200, 70);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Puntuación: " + puntuacion, 20, 30);
        g.drawString("Oro: " + oroRecogido, 20, 50);
        g.drawString("Frutas: " + frutasRecogidas, 20, 70);
    }

    private void dibujarFondo(Graphics g) {
        if (fondoImagen != null) {
            // Dibujar imagen de fondo
            g.drawImage(fondoImagen, -posicionFondo % ANCHO, 0, ANCHO, ALTO, null);
            g.drawImage(fondoImagen, ANCHO - posicionFondo % ANCHO, 0, ANCHO, ALTO, null);
        } else {
            // Dibujo de respaldo
            g.setColor(new Color(135, 206, 235));
            g.fillRect(0, 0, ANCHO, SUELO);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (juegoActivo) {
            // Actualizar estado del juego
            if (!sistemaPregunta.estaActiva()) {
                moverObjetos();
                verificarColisiones();

                // Verificar si es momento de iniciar la batalla con el jefe
                if (puntuacion >= 3000 && !modoBatalla && jefeFinal == null) {
                    iniciarBatallaJefe();
                }

                if (mostrandoEfectoFuego) {
                    efectoFuegoJefe.actualizar();
                    efectoFuegoGuerrero.actualizar();

                    // Si ambos efectos han terminado, continuar el juego
                    if (!efectoFuegoJefe.estaActivo() && !efectoFuegoGuerrero.estaActivo()) {
                        mostrandoEfectoFuego = false;
                    }
                }

                // Generar elementos solo si no estamos en modo batalla
                if (!modoBatalla) {
                    // Lógica alternada para árboles y obstáculos
                    if (siguienteEsArbol) {
                        if (random.nextInt(300) < 10) { // ~3% de probabilidad
                            int tipo = random.nextInt(2); // 0-1: los dos tipos de árboles
                            elementosAmbiente.add(new ElementoAmbiente(ANCHO, SUELO, tipo));
                            siguienteEsArbol = false; // El siguiente será un obstáculo
                        }
                    } else {
                        if (obstaculos.isEmpty()
                                || obstaculos.get(obstaculos.size() - 1).getX() < ANCHO - random.nextInt(300) - 200) {
                            int tipo = random.nextInt(7); // 0-6: incluir todos los tipos de obstáculos

                            // Evitar rocas (tipo 5)
                            if (tipo == 5) {
                                tipo = random.nextInt(5); // 0-4
                            }

                            int subtipo = 1;

                            if (tipo == 2) { // Pinchos
                                subtipo = random.nextInt(3) + 1;
                            } else if (tipo == 4) { // Jaguar
                                subtipo = random.nextInt(3) + 1;
                            } else if (tipo == 6) { // Ruinas
                                subtipo = random.nextInt(2) + 1;
                            }

                            obstaculos.add(new Obstaculo(ANCHO, SUELO, tipo, subtipo));
                            siguienteEsArbol = true; // El siguiente será un árbol
                        }
                    }

                    generarColeccionable();
                    generarAguila();
                    puntuacion++;

                    // Aumentar dificultad
                    if (puntuacion % 500 == 0) {
                        velocidad++;
                    }
                } else {
                    // En modo batalla
                    actualizarBatallaJefe();
                }
            }
        }
        repaint();
    }

    private void iniciarBatallaJefe() {
        modoBatalla = true;
        jefeFinal = new JefeFinal(600, 50);
        siguienteEsArbol = true;

        // Limpiar elementos del juego normal
        obstaculos.clear();
        aguilas.clear();
        coleccionables.clear();

        // Dejar solo algunos elementos de ambiente para decoración
        for (int i = elementosAmbiente.size() - 1; i >= 0; i--) {
            if (random.nextBoolean()) {
                elementosAmbiente.remove(i);
            }
        }
    }

    private void actualizarBatallaJefe() {
        if (jefeFinal != null) {
            jefeFinal.mover();

            // Si estamos esperando un ataque y no hay pregunta activa, generar una
            if (esperandoAtaque && !sistemaPregunta.estaActiva()) {
                sistemaPregunta.generarPreguntaAleatoria();
                esperandoAtaque = false;
            }

            // Actualizar animación de ataque si está atacando
            if (atacando) {
                long tiempoActual = System.currentTimeMillis();
                if (tiempoActual - ultimoCambioFrameAtaque > DURACION_FRAME_ATAQUE) {
                    frameAtaqueActual++;
                    if (frameAtaqueActual >= framesAtaque.length) {
                        frameAtaqueActual = 0;
                        atacando = false;
                    }
                    ultimoCambioFrameAtaque = tiempoActual;
                }
            }
        }
    }

    private void moverObjetos() {
        guerrero.mover();

        // Mover obstáculos
        for (int i = 0; i < obstaculos.size(); i++) {
            Obstaculo obs = obstaculos.get(i);
            obs.mover(velocidad);

            if (obs.getX() < -100) {
                obstaculos.remove(i);
                i--;
            }
        }

        // Mover elementos ambientales
        for (int i = 0; i < elementosAmbiente.size(); i++) {
            ElementoAmbiente elem = elementosAmbiente.get(i);
            elem.mover(velocidad);

            if (elem.getX() < -100) {
                elementosAmbiente.remove(i);
                i--;
            }
        }

        // Mover águilas
        for (int i = 0; i < aguilas.size(); i++) {
            Aguila aguila = aguilas.get(i);
            aguila.mover(velocidad);

            if (aguila.getX() < -100) {
                aguilas.remove(i);
                i--;
            }
        }

        // Mover coleccionables
        for (int i = 0; i < coleccionables.size(); i++) {
            Coleccionable col = coleccionables.get(i);
            col.mover(velocidad);

            if (col.getX() < -50) {
                coleccionables.remove(i);
                i--;
            }
        }

        // Mover fondo
        posicionFondo = (posicionFondo + velocidad / 2);
    }

    private void verificarColisiones() {
        Rectangle hitboxGuerrero = guerrero.getHitbox();

        // Verificar colisiones con obstáculos
        for (Obstaculo obs : obstaculos) {
            if (hitboxGuerrero.intersects(obs.getHitbox())) {
                juegoActivo = false;
                timer.stop();
                return;
            }
        }

        // Ya no verificamos colisiones con águilas, ahora son decorativas
        // Verificar colisiones con coleccionables
        for (int i = 0; i < coleccionables.size(); i++) {
            Coleccionable col = coleccionables.get(i);
            if (hitboxGuerrero.intersects(col.getHitbox())) {
                if (col.getTipo() == 0) { // Oro
                    oroRecogido++;
                    puntuacion += 100;
                } else { // Fruta
                    frutasRecogidas++;
                    puntuacion += 50;
                }
                coleccionables.remove(i);
                i--;
            }
        }
    }

    private void reiniciarJuego() {
        obstaculos.clear();
        coleccionables.clear();
        elementosAmbiente.clear();
        aguilas.clear(); // Limpiar águilas
        iniciarJuego();
        timer.start();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Guerrero Azteca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new PantallaCarga(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}