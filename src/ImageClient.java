import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;

public class ImageClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        int numPanels = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of video panels:"));
        JTextField[] fpsFields = new JTextField[numPanels];
        JPanel panel = new JPanel(new GridLayout(numPanels, 2));
        for (int i = 0; i < numPanels; i++) {
            panel.add(new JLabel("Enter FPS for panel " + (i + 1) + ": "));
            fpsFields[i] = new JTextField();
            panel.add(fpsFields[i]);
        }
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter FPS for each panel", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        int[] frameIntervals = new int[numPanels];
        for (int i = 0; i < numPanels; i++) {
            int fps = Integer.parseInt(fpsFields[i].getText());
            frameIntervals[i] = 1000 / fps;
        }

        JFrame frame = new JFrame("Multi-Stream Video Client");
        frame.setLayout(new GridLayout((int) Math.ceil(numPanels / 2.0), 2));
        frame.setSize(1600, 1200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        VideoPanel[] videoPanels = new VideoPanel[numPanels];
        for (int i = 0; i < numPanels; i++) {
            videoPanels[i] = new VideoPanel(frameIntervals[i]);
            frame.add(videoPanels[i]);
        }

        frame.setVisible(true);

        for (VideoPanel videoPanel : videoPanels) {
            new Thread(() -> {
                try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                     InputStream inputStream = socket.getInputStream();
                     DataInputStream dataInputStream = new DataInputStream(inputStream)) {

                    System.out.println("Connected to server");

                    while (true) {
                        long startTime = System.currentTimeMillis();

                        int imageSize = dataInputStream.readInt(); // Read image size
                        if (imageSize <= 0) {
                            System.out.println("No more images to read, exiting");
                            break;
                        }

                        byte[] imageBytes = new byte[imageSize];
                        dataInputStream.readFully(imageBytes); // Read image data

                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        if (image == null) {
                            System.out.println("Failed to decode image, exiting");
                            break;
                        }

                        videoPanel.displayImage(image);

                        long endTime = System.currentTimeMillis();
                        long decodeTime = endTime - startTime;
                        long sleepTime = videoPanel.getFrameInterval() - decodeTime;
                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    System.out.println("Client disconnected");
                }
            }).start();
        }
    }
}