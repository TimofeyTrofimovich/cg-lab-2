import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageProcessingApp extends JFrame {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private Mat image;
    private JLabel imageLabel;

    // Ползунки для поэлементной операции
    private JSlider alphaSlider;
    private JSlider betaSlider;

    public ImageProcessingApp() {
        setTitle("Image Processing App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Панель для кнопок
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 1));

        JButton loadButton = new JButton("Load Image");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });

        alphaSlider = new JSlider(0, 300, 100);
        alphaSlider.setBorder(BorderFactory.createTitledBorder("Alpha (Contrast)"));
        betaSlider = new JSlider(-100, 100, 0);
        betaSlider.setBorder(BorderFactory.createTitledBorder("Beta (Brightness)"));

        JButton elementwiseButton = new JButton("Apply Element-wise Operation");
        elementwiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyElementwiseOperation();
            }
        });

        JButton contrastButton = new JButton("Apply Linear Contrast");
        contrastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyLinearContrast();
            }
        });

        JButton morphologyButton = new JButton("Apply Morphology");
        morphologyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyMorphology();
            }
        });

        // Добавляем кнопки на панель управления
        controlPanel.add(loadButton);
        controlPanel.add(alphaSlider);
        controlPanel.add(betaSlider);
        controlPanel.add(elementwiseButton);
        controlPanel.add(contrastButton);
        controlPanel.add(morphologyButton);

        // Настройка основного интерфейса
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
            displayImage(image);
        }
    }

    private void displayImage(Mat img) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        ImageIcon icon = new ImageIcon(matOfByte.toArray());
        imageLabel.setIcon(icon);
        pack();
    }

    private void applyElementwiseOperation() {
        if (image != null) {
            double alpha = alphaSlider.getValue() / 100.0;
            int beta = betaSlider.getValue();

            Mat result = new Mat();
            image.convertTo(result, -1, alpha, beta);
            displayImage(result);
        }
    }

    private void applyLinearContrast() {
        if (image != null) {
            Mat result = new Mat();
            Core.normalize(image, result, 0, 255, Core.NORM_MINMAX);
            displayImage(result);
        }
    }

    private void applyMorphology() {
        if (image != null) {
            Mat result = new Mat();
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
            Imgproc.dilate(image, result, kernel);
            displayImage(result);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageProcessingApp app = new ImageProcessingApp();
                app.setVisible(true);
            }
        });
    }
}
