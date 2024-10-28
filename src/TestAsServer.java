import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestAsServer {

    public static void main(String[] args) throws InterruptedException {
        // запускаємо сервер на порту 3345
        try (ServerSocket server = new ServerSocket(3345)) {

            // переходимо в стан очікування підключення до сокету
            Socket client = server.accept();

            // після хендшейкінга сервер асоціює підключеного клієнта з сокетом client
            System.out.print("Connection accepted.");

            // ініціюємо потоки для спілкування в сокеті
            // потік запису в сокет
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            System.out.println("DataOutputStream created");

            // потік читання з сокету
            DataInputStream in = new DataInputStream(client.getInputStream());
            System.out.println("DataInputStream created");

            // починаємо діалог з підключеним клієнтом в циклі, поки сокет не закритий
            while (!client.isClosed()) {
                System.out.println("Server reading from channel");

                // сервер очікує на дані від клієнта в потоці для читання
                String entry = in.readUTF();

                // після отримання даних зчитує їх
                System.out.println("READ from client message - " + entry);
                Thread.sleep(1000);
                // і виводить в консоль
                System.out.println("Server try writing to channel");

                // ініціалізація перевірки умови завершення роботи клієнта із сервером,
                // для цього клієнт повинен прислати повідомлення із ключовим словом - quit
                if (entry.equalsIgnoreCase("quit")) {
                    System.out.println("Client initialize connections suicide ...");
                    out.writeUTF("Server reply - " + entry + " - OK");
                    out.flush();
                    Thread.sleep(3000);
                    break;
                }

                // якщо умова не справджується – продовжуємо роботу – відправляємо ехо-відповідь клієнту
                out.writeUTF("Server reply - " + entry + " - OK");
                System.out.println("Server wrote message to client.");

                /* звільнюємо буфер мережевих повідомлень (по замовчуванню повідомлення
                не відразу відправляються в мережу, а накопичуються в спеціальному буфері
                повідомлень, розмір якого визначається конкретними налаштуваннями
                системи, а метод - flush() відправляє повідомлення не очікуючи заповнення буфера */
                out.flush();
            }

            // якщо умова виходу справдилась – закриваємо з’єднання
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закриваємо спочатку потоки сокета!
            in.close();
            out.close();

            // потім закриваємо сам сокет спілкування на стороні сервера!
            client.close();

            // при виході з try-with-resources закривається серверний сокет.
            // У багатопотоковому сервері його закривати не потрібно до моменту
            // завершення роботи всього сервера.
            System.out.println("Closing connections & channels - DONE.");

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}