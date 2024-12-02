import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ImageServer {
    private static final int PORT = 12345;
    private static final String IMAGE_DIR = "compressed_video";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                for (int i = 1; i <= 23332; i++) {
                    if (socket.isClosed()) {
                        System.out.println("Socket is closed, stopping transmission");
                        break;
                    }
                    String fileName = String.format("compressed_frame_%04d.jpg", i);
                    File imageFile = new File(IMAGE_DIR, fileName);
                    if (imageFile.exists()) {
                        System.out.println("Sending " + fileName);
                        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

                        dataOutputStream.writeInt(imageBytes.length); // Send image size
                        dataOutputStream.write(imageBytes); // Send image data
                        dataOutputStream.flush();
                    } else {
                        System.out.println(fileName + " does not exist");
                    }
                }
            } catch (SocketException ex) {
                System.out.println("Client disconnected: " + ex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                    System.out.println("Socket closed");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}