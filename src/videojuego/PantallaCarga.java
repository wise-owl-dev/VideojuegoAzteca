package videojuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PantallaCarga extends JPanel implements ActionListener {

    private static final int ANCHO = 800;
    private static final int ALTO = 300;
    private Image fondoCarga;
    private Timer timerCarga;
    private JFrame parentFrame;

    // Variables simplificadas para la carga
    private int progresoActual = 0;
    private boolean cargaCompletada = false;

    // Elementos visuales
    private int posYTitulo = 80;
    private boolean tituloSubiendo = false;
    private Font fuenteTitulo;
    private Font fuenteNormal;

    private MusicaManager musicaManager;

    public PantallaCarga(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);

        // Intentar cargar la imagen de fondo
        try {
            fondoCarga = ImageIO.read(getClass().getResource("/imagenes/fondos/fondo_carga.jpg"));
            System.out.println("Imagen de fondo cargada correctamente");
        } catch (IOException e) {
            System.out.println("Error al cargar imagen de fondo: " + e.getMessage());
            fondoCarga = null;
        }

        // Configurar fuentes
        fuenteTitulo = new Font("Serif", Font.BOLD, 40);
        fuenteNormal = new Font("SansSerif", Font.PLAIN, 16);

        try {
            // Intenta cargar una fuente personalizada si existe
            fuenteTitulo = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fuentes/azteca.ttf")).deriveFont(40f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fuenteTitulo);
            System.out.println("Fuente personalizada cargada correctamente");
        } catch (Exception e) {
            System.out.println("Usando fuente por defecto: " + e.getMessage());
        }

        musicaManager = MusicaManager.getInstancia();
        musicaManager.reproducir("menu");

        // Iniciar el timer con periodo más lento para ver mejor la carga
        timerCarga = new Timer(50, this); // 10 FPS para ver mejor la animación
        timerCarga.start();
        System.out.println("Timer iniciado");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar fondo
        if (fondoCarga != null) {
            g.drawImage(fondoCarga, 0, 0, ANCHO, ALTO, null);
        } else {
            // Fondo de respaldo con color sólido
            g.setColor(new Color(80, 20, 20));
            g.fillRect(0, 0, ANCHO, ALTO);
        }

        /*
         * // Dibujar título
         * g.setFont(fuenteTitulo);
         * g.setColor(new Color(0, 0, 0, 200));
         * g.drawString("GUERRERO AZTECA", ANCHO / 2 - 190 + 3, posYTitulo + 3); //
         * Sombra
         * g.setColor(new Color(255, 215, 0)); // Color dorado
         * g.drawString("GUERRERO AZTECA", ANCHO / 2 - 190, posYTitulo);
         */
        // Dibujar barra de carga - IMPORTANTE
        int anchoBarraCarga = 400;
        int altoBarraCarga = 20;
        int xBarraCarga = (ANCHO - anchoBarraCarga) / 2;
        int yBarraCarga = ALTO - 80;

        // Fondo de la barra
        g.setColor(new Color(50, 30, 20));
        g.fillRect(xBarraCarga, yBarraCarga, anchoBarraCarga, altoBarraCarga);

        // Progreso de la barra - Simplificado para asegurar su funcionamiento
        int anchoBarra = (int) (anchoBarraCarga * progresoActual / 100.0);
        g.setColor(new Color(255, 215, 0)); // Color dorado
        // g.setColor(new Color(80, 200, 120));
        // g.setColor(new Color(130, 174, 186));
        g.fillRect(xBarraCarga, yBarraCarga, anchoBarra, altoBarraCarga);

        // Borde de la barra
        g.setColor(new Color(120, 80, 40));
        g.drawRect(xBarraCarga, yBarraCarga, anchoBarraCarga, altoBarraCarga);

        // Texto de carga
        g.setFont(fuenteNormal);
        g.setColor(Color.WHITE);
        // String mensajeCarga = "Cargando recursos... " + progresoActual + "%";
        // g.drawString(mensajeCarga, xBarraCarga, yBarraCarga + altoBarraCarga + 25);

        // Texto de depuración - quitar después
        g.setColor(Color.WHITE);
        g.drawString("Progreso actual: " + progresoActual, 20, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Animar título
        if (tituloSubiendo) {
            posYTitulo--;
            if (posYTitulo <= 75)
                tituloSubiendo = false;
        } else {
            posYTitulo++;
            if (posYTitulo >= 85)
                tituloSubiendo = true;
        }

        // Incrementar progreso - SIMPLIFICADO
        if (progresoActual < 100) {
            // Incremento lento (2% por paso)
            progresoActual += 2;

            // Si llegamos al final, pasar a la siguiente pantalla
            if (progresoActual >= 100) {
                progresoActual = 100;
                cargaCompletada = true;
                System.out.println("Carga completada: " + progresoActual);
                finalizarCarga();
            }
        }

        // Imprimir progreso cada 10%
        if (progresoActual % 10 == 0) {
            System.out.println("Progreso: " + progresoActual + "%");
        }

        repaint();
    }

    private void finalizarCarga() {
        // Detener el timer
        timerCarga.stop();

        // Pequeña pausa para mostrar el 100% antes de avanzar
        Timer pausaFinal = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarPantallaInicio();
                ((Timer) e.getSource()).stop();
            }
        });
        pausaFinal.setRepeats(false);
        pausaFinal.start();
    }

    private void iniciarPantallaInicio() {
        System.out.println("Iniciando pantalla de inicio");

        // Ir a la pantalla de inicio
        try {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new PantallaInicio(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        } catch (Exception e) {
            System.out.println("Error al iniciar pantalla de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}