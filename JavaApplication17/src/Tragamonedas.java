import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tragamonedas extends JFrame {

    private static final int NUM_IMAGENES = 16;
    private static final int NUM_COLUMNAS = 3;
    private static final int NUM_FILAS = 1;
    private static final int ANCHO_IMAGEN = 100;
    private static final int ALTO_IMAGEN = 100;
    private static final int ESPACIO_ENTRE_IMAGENES = 10;
    private static final int PUNTOS_REPETICION_3 = 10;
    private static final int PUNTOS_REPETICION_2 = 5;
    private static final double PROBABILIDAD_REPETICION = 0.7; // Mayor probabilidad de repetición

    private List<ImageIcon> iconos;
    private List<JLabel> labels;
    private List<HiloImagen> hilos;
    private JLabel lblImagenNueva;
    private JButton btnIniciar;
    private int puntos;

    public Tragamonedas() {
        iconos = new ArrayList<>();
        labels = new ArrayList<>();
        hilos = new ArrayList<>();
        puntos = 0;

        setTitle("Tragamonedas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Agregar los iconos de las imágenes
        for (int i = 1; i <= NUM_IMAGENES; i++) {
            ImageIcon icono = new ImageIcon("imagen" + i + ".png");
            iconos.add(icono);
           
        }
        // Agregar la imagen detrás de las casillas
ImageIcon imagenDetras = new ImageIcon("tragamonedas.png"); // Reemplaza "tu_imagen.png" con el nombre de tu imagen
Image imagenRedimensionada = imagenDetras.getImage().getScaledInstance(ANCHO_IMAGEN, ALTO_IMAGEN, Image.SCALE_DEFAULT);
ImageIcon imagenDetrasRedimensionada = new ImageIcon(imagenRedimensionada);
iconos.add(imagenDetrasRedimensionada);

        // Crear los labels de las imágenes
        for (int i = 0; i < NUM_COLUMNAS * NUM_FILAS; i++) {
            JLabel label = new JLabel(iconos.get(0));
            label.setPreferredSize(new Dimension(ANCHO_IMAGEN, ALTO_IMAGEN));
            labels.add(label);
            add(label);
        }
        
        // Label para la imagen nueva
        lblImagenNueva = new JLabel();
        lblImagenNueva.setPreferredSize(new Dimension(ANCHO_IMAGEN, ALTO_IMAGEN));
        add(lblImagenNueva);

        // Botón de inicio
        btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });
        add(btnIniciar);

        pack();
        setLocationRelativeTo(null);
    }

    private void iniciarJuego() {
        // Deshabilitar el botón mientras se ejecuta el juego
        btnIniciar.setEnabled(false);

        // Detener hilos existentes
        detenerHilos();

        // Crear nuevos hilos
        for (int i = 0; i < NUM_COLUMNAS; i++) {
            HiloImagen hilo = new HiloImagen(labels.get(i));
            hilos.add(hilo);
            hilo.start();
        }

        // Esperar a que todos los hilos terminen
        try {
            for (HiloImagen hilo : hilos) {
                hilo.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verificar si hay repetición en las imágenes
        int repeticiones = verificarRepeticiones();
        if (repeticiones == 3) {
            puntos += PUNTOS_REPETICION_3;
            JOptionPane.showMessageDialog(this, "¡Ganaste " + PUNTOS_REPETICION_3 + " puntos por 3 frutas repetidas!");
        } else if (repeticiones == 2) {
            puntos += PUNTOS_REPETICION_2;
            JOptionPane.showMessageDialog(this, "¡Ganaste " + PUNTOS_REPETICION_2 + " puntos por 2 frutas repetidas!");
        }

        // Actualizar la barra de título con los puntos ganados
        setTitle("Tragamonedas - Puntos: " + puntos);

        // Habilitar el botón de nuevo
        btnIniciar.setEnabled(true);
    }

    private void detenerHilos() {
        for (HiloImagen hilo : hilos) {
            hilo.detener();
        }
        hilos.clear();
    }

    private int verificarRepeticiones() {
        int repeticiones = 0;
        ImageIcon imagenBase = ((ImageIcon) labels.get(0).getIcon());
        for (JLabel label : labels) {
            if (((ImageIcon) label.getIcon()).equals(imagenBase)) {
                repeticiones++;
            }
        }
        return repeticiones;
    }

    private class HiloImagen extends Thread {
        private JLabel label;
        private boolean detenido;

        public HiloImagen(JLabel label) {
            this.label = label;
            this.detenido = false;
        }

        public void detener() {
            detenido = true;
        }

        @Override
        public void run() {
            Random random = new Random();
            int contador = 0;
            while (!detenido && contador < 10) {
                int indice = random.nextInt(NUM_IMAGENES);
                if (contador > 0 && random.nextDouble() < PROBABILIDAD_REPETICION) {
                    // Repetir la imagen actual
                    indice = iconos.indexOf(label.getIcon());
                }
                ImageIcon icono = iconos.get(indice);
                label.setIcon(icono);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                contador++;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tragamonedas().setVisible(true));
    }
}
