import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestAsClient {

    public static void main(String[] args) throws InterruptedException {
        // запускаємо підключення сокета по відомих адресі та порту сервера,
        // ініціалізуємо прийом повідомлень з консолі клієнта
        try (Socket socket = new Socket("localhost", 3345);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
             DataInputStream ois = new DataInputStream(socket.getInputStream());) {
            System.out.println("Client connected to socket.");
            System.out.println();
            System.out.println("Client writing channel = oos & reading channel = ois initialized.");

            // в окремому потоці запускаємо читання відповідей сервера
            Thread readThread = new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        String in = ois.readUTF(); // блокується, якщо сервер не надсилає даних
                        System.out.println("Server response: " + in);
                    }
                } catch (IOException e) {
                    System.err.println("Connection closed or server not responding.");
                }
            });
            readThread.start();


            // поки відкритий вихідний потік сокета виконуємо наступні дії
            while (!socket.isOutputShutdown()) {

                // очікуємо вводу в консоль клієнта
                if (br.ready()) {

                    // якщо дані появились – зчитуємо їх у стрічку
                    System.out.println("Client start writing in channel...");
                    Thread.sleep(1000);
                    String clientCommand = br.readLine();

                    // записуємо стрічку повідомлення у потік сокета для сервера
                    oos.writeUTF(clientCommand);
                    oos.flush();
                    System.out.println("Client sent message " + clientCommand + " to server.");
                    Thread.sleep(1000);

                    // очікуємо поки сервер зчитає повідомлення та відповість
                    // перевіряємо умову виходу із з’єднання
                    if (clientCommand.equalsIgnoreCase("quit")) {

                        // якщо умова виходу справдилась – роз’єднуємось
                        System.out.println("Client kill connections");
                        Thread.sleep(2000);

                        System.out.println("reading...");
                        String in = ois.readUTF();
                        System.out.println(in);

                        // виходимо із циклу
                        break;
                    }

                    // якщо умова не справджується – продовжуємо роботу
                    System.out.println("Client sent message & start waiting for data from server...");

                }
            }

            // на виході із циклу спілкування закриваємо всі ресурси
            System.out.println("Closing connections & channels on client side - DONE.");
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}