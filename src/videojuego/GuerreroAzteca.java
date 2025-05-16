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
    private int faseJefeActual = 0; // Fase actual del jefe (0 = no iniciada, 1 = primer jefe, etc.)
    private int puntosParaSiguienteFase = 1000; // Puntos necesarios para el primer jefe
    private boolean jefeCompletado = false; // Indica si el jefe actual ha sido derrotado
    private int distanciaParaReaparicionJefe = 0; // Contará la distancia después de derrotar a un jefe
    private String mensajeTemporal = null;
    private long tiempoFinMensaje = 0;

    // Imágenes de ataque del guerrero
    private Image[] framesAtaque;
    private int frameAtaqueActual;
    private boolean atacando;
    private long ultimoCambioFrameAtaque;
    private static final long DURACION_FRAME_ATAQUE = 100;

    // Vidas del jugador
    private int vidasJugador;
    private Image imagenCorazon;

    private boolean juegoGanado = false;

    private MusicaManager musicaManager;

    public GuerreroAzteca() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(new Color(255, 220, 180));
        setFocusable(true);
        addKeyListener(this);
        cargarImagenes();
        iniciarJuego();
        musicaManager = MusicaManager.getInstancia();
        if (!musicaManager.getMusicaActual().equals("juego")) {
            musicaManager.reproducir("juego");
        }
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

            musicaManager = MusicaManager.getInstancia();

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
        velocidad = 4;
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

        // Reiniciar variables de fase del jefe
        faseJefeActual = 0;
        puntosParaSiguienteFase = 1000; // Puntos iniciales para el primer jefe
        jefeCompletado = false;
        distanciaParaReaparicionJefe = 0;
        juegoGanado = false;

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
        // Determinar qué escenario dibujar (batalla o normal)
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

        // Dibujar mensaje temporal si existe
        if (mensajeTemporal != null) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();
            int anchoTexto = fm.stringWidth(mensajeTemporal);

            // Fondo semitransparente
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect((ANCHO - anchoTexto) / 2 - 10, ALTO / 2 - 20, anchoTexto + 20, 40);

            // Texto
            g.setColor(Color.YELLOW);
            g.drawString(mensajeTemporal, (ANCHO - anchoTexto) / 2, ALTO / 2 + 10);
        }

        // Dibujar sistema de preguntas por encima de todo
        // Pero solo si el guerrero no está herido
        if (sistemaPregunta.estaActiva() && !guerrero.estaHerido()) {
            sistemaPregunta.dibujar(g, ANCHO, ALTO);
        }

        // Dibujar efectos de fuego
        if (efectoFuegoJefe != null && efectoFuegoJefe.estaActivo()) {
            efectoFuegoJefe.dibujar(g);
        }

        if (efectoFuegoGuerrero != null && efectoFuegoGuerrero.estaActivo()) {
            efectoFuegoGuerrero.dibujar(g);
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

        // Dibujar jefe final (se mantiene barra de vida visual, pero sin números)
        if (jefeFinal != null) {
            jefeFinal.dibujar(g);

            // Opcional: Mostrar un mensaje más misterioso sin información específica
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("¡Ataca al jefe!", 600, 30);
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
        if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) && (!juegoActivo || juegoGanado)) {
            System.out.println("Reiniciando juego desde estado: activo=" + juegoActivo + ", ganado=" + juegoGanado);
            reiniciarJuego();
            return;
        }
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
            if (esCorrecta) {
                // Mostrar efecto de fuego en el jefe inmediatamente
                int xFuego = jefeFinal.getX() + 100;
                int yFuego = jefeFinal.getY() + 80;
                efectoFuegoJefe.iniciar(xFuego, yFuego);
                mostrandoEfectoFuego = true;

                // Quitar vida directamente al jefe sin usar timer
                boolean jefeVencido = jefeFinal.perdioVida();
                puntuacion += 500;

                // Comprobar si el jefe fue vencido
                if (jefeVencido) {
                    System.out.println("¡JEFE VENCIDO! Fase: " + faseJefeActual);

                    if (faseJefeActual >= 3) {
                        // VICTORIA FINAL
                        musicaManager.cambiarA("victoria");
                        System.out.println("VICTORIA DETECTADA - Fase " + faseJefeActual);

                        // Animar brevemente la victoria antes de mostrar la pantalla final
                        Timer timerVictoria = new Timer(1500, ev -> {
                            mostrarFinJuego(true);
                            ((Timer) ev.getSource()).stop();
                        });
                        timerVictoria.setRepeats(false);
                        timerVictoria.start();
                    } else {
                        // Fase completada, continuar al juego normal
                        musicaManager.cambiarA("juego");
                        puntuacion += 1000 * faseJefeActual;
                        jefeCompletado = true;
                        modoBatalla = false;
                        jefeFinal = null;
                        distanciaParaReaparicionJefe = 0;
                        puntosParaSiguienteFase += 3000 * faseJefeActual;

                        // Mensaje de victoria sin revelar la fase
                        mostrarMensajeTemporal("¡Jefe derrotado! Continúa tu aventura...", 2000);

                    }
                }
            } else {
                // Respuesta incorrecta: Mostrar fuego en el guerrero
                int xFuego = guerrero.getX() + 25;
                int yFuego = guerrero.getY() - 50;
                efectoFuegoGuerrero.iniciar(xFuego, yFuego);
                mostrandoEfectoFuego = true;

                // Quitar vida directamente
                vidasJugador--;

                if (vidasJugador <= 0) {
                    // MODIFICACIÓN: Asegurarse de que se desactive cualquier pregunta activa
                    if (sistemaPregunta.estaActiva()) {
                        sistemaPregunta.desactivar();
                    }

                    // MODIFICACIÓN: Esperar a que termine el efecto de fuego antes de iniciar la
                    // animación de herido
                    Timer timerInicioHerido = new Timer(800, ev -> {
                        // Iniciar animación de herido
                        guerrero.iniciarAnimacionHerido();
                        System.out.println("Iniciando animación de herido por pérdida de vidas");

                        // La transición a la pantalla final se manejará en actionPerformed
                        // cuando se detecte que la animación de herido ha terminado

                        ((Timer) ev.getSource()).stop();
                    });
                    timerInicioHerido.setRepeats(false);
                    timerInicioHerido.start();
                }
            }
        }
    }

    private void mostrarFinJuego(boolean victoria) {
        juegoActivo = false;
        juegoGanado = victoria;
        timer.stop();

        if (victoria) {
            musicaManager.cambiarA("victoria");
        } else {
            musicaManager.cambiarA("derrota");
        }

        // Desactivar cualquier pregunta pendiente
        if (sistemaPregunta != null && sistemaPregunta.estaActiva()) {
            sistemaPregunta.desactivar();
        }

        // Mostrar la pantalla final estilizada
        PantallaFinal pantallaFinal = new PantallaFinal(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                victoria,
                puntuacion,
                oroRecogido,
                frutasRecogidas);

        // Reemplazar este panel con la pantalla final
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.add(pantallaFinal);
        frame.revalidate();
        frame.repaint();
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
        // Verificar si el guerrero está herido o el juego está activo
        if (juegoActivo || guerrero.estaHerido()) {
            // Si el guerrero está herido, actualizar solo la animación de herido
            if (guerrero.estaHerido()) {
                guerrero.mover(); // Esto actualizará la animación de herido

                // Si la animación de herido ha terminado, mostrar pantalla final
                if (guerrero.esAnimacionHeridoCompleta()) {
                    // Una pequeña pausa antes de mostrar la pantalla final
                    Timer timerFinJuego = new Timer(500, ev -> {
                        mostrarFinJuego(false); // Mostrar pantalla de derrota
                        ((Timer) ev.getSource()).stop();
                    });
                    timerFinJuego.setRepeats(false);
                    timerFinJuego.start();

                    // Desactivar este timer principal para evitar llamadas múltiples
                    timer.stop();
                }

                repaint();
                return; // Terminar aquí si el guerrero está herido
            }

            // Código normal del juego (solo se ejecuta si el guerrero NO está herido)
            // Actualizar estado del juego
            if (!sistemaPregunta.estaActiva()) {
                moverObjetos();
                verificarColisiones();

                // Si el juego terminó por colisión, no procesar más lógica
                if (!juegoActivo) {
                    repaint();
                    return;
                }

                // Actualizar mensajes temporales
                if (mensajeTemporal != null && System.currentTimeMillis() > tiempoFinMensaje) {
                    mensajeTemporal = null;
                }

                // Verificar si es momento de iniciar la batalla con el jefe
                if (!modoBatalla && jefeFinal == null) {
                    if (faseJefeActual == 0 && puntuacion >= puntosParaSiguienteFase) {
                        // Primera aparición del jefe
                        iniciarBatallaJefe();
                    } else if (jefeCompletado) {
                        // Si ya derrotamos una fase, contar distancia para reaparición
                        distanciaParaReaparicionJefe += velocidad;

                        // Después de cierta distancia, que aparezca el siguiente jefe
                        if (distanciaParaReaparicionJefe > 5000 && puntuacion >= puntosParaSiguienteFase) {
                            iniciarBatallaJefe();
                        }
                    }
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
                    distanciaParaReaparicionJefe++; // Incrementar también el contador de distancia

                    // Aumentar dificultad
                    if (puntuacion % 1000 == 0 && velocidad < 7) { // Incremento más lento y máximo más bajo
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
        faseJefeActual++;
        jefeCompletado = false;

        // Crear jefe según la fase actual (sin cambios en la lógica interna)
        jefeFinal = new JefeFinal(600, 50, faseJefeActual);
        siguienteEsArbol = true;

        musicaManager.cambiarA("batalla");

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

        // Mostrar un mensaje más misterioso sin revelar detalles específicos
        String mensajeFase = "¡Ha aparecido un nuevo jefe! Derrótalo respondiendo correctamente.";
        mostrarMensajeTemporal(mensajeFase, 3000); // Mostrar durante 3 segundos
    }

    private void mostrarMensajeTemporal(String mensaje, int duracionMs) {
        mensajeTemporal = mensaje;
        tiempoFinMensaje = System.currentTimeMillis() + duracionMs;
    }

    private void actualizarBatallaJefe() {
        // Si el guerrero está herido, no actualizar la batalla
        if (guerrero.estaHerido()) {
            return;
        }

        if (jefeFinal != null) {
            try {
                jefeFinal.mover();

                // Verificar si ya hemos ganado el juego
                if (juegoGanado) {
                    // Si ya ganamos, no generar más preguntas
                    return;
                }

                // Si estamos esperando un ataque y no hay pregunta activa, generar una
                if (esperandoAtaque && !sistemaPregunta.estaActiva()) {
                    sistemaPregunta.generarPreguntaAleatoria();
                    esperandoAtaque = false;
                    System.out.println("Pregunta generada - Fase: " + faseJefeActual +
                            ", Vidas restantes jefe: " + jefeFinal.getVidasRestantes() + "/" +
                            jefeFinal.getVidasTotales() +
                            ", Vidas jugador: " + vidasJugador);
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
            } catch (Exception e) {
                // Evitar que una excepción bloquee el juego
                System.out.println("Error en actualizarBatallaJefe: " + e.getMessage());
                e.printStackTrace();
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
                // Iniciar animación de herido
                guerrero.iniciarAnimacionHerido();

                musicaManager.cambiarA("derrota");
                // El juego termina pero seguimos actualizando para mostrar la animación
                juegoActivo = false;
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
        // Detener cualquier timer que pueda estar corriendo
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        musicaManager.cambiarA("juego");
        // Limpiar elementos actuales
        obstaculos.clear();
        coleccionables.clear();
        elementosAmbiente.clear();
        aguilas.clear();

        // Restablecer todas las variables críticas
        juegoGanado = false;
        modoBatalla = false;
        mostrandoEfectoFuego = false;

        // Inicializar un juego nuevo
        iniciarJuego();

        // Asegurarse de que el timer está corriendo
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }

        System.out.println("Juego reiniciado completamente");
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