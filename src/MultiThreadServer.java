import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        // запускаємо сервер на порту 3345 та ініціалізуємо змінну для обробки консольних команд з самого сервера
        try (ServerSocket server = new ServerSocket(3345);
             BufferedReader br = new BufferedReader(new
                     InputStreamReader(System.in))) {
            System.out.println("Server socket created, command console reader for listen to server commands ");

            // запускаємо цикл при умові, що серверний сокет не закритий
            while (!server.isClosed()) {

                // провіряємо чи отримали команди із консолі сервера
                if (br.ready()) {
                    System.out.println("Main Server found any messages in channel, let 's look at them.");

                    /* якщо присутня команда – quit, то ініціалізуємо
                    закриття сервера і вихід із циклу роздачі потоків */
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        System.out.println("Main Server initiate exiting...");
                        server.close();
                        break;
                    }
                }

                // якщо команди від сервера нема, то переходимо в стан очікування підключення
                Socket client = server.accept();

                // після отримання запиту на підключення сервер створює сокет для спілкування з клієнтом
                // і передає його підсерверу для подальшого спілкування з клієнтом
                executeIt.execute(new MonoThreadClientHandler(client));
                System.out.print("Connection accepted.");
            }

            // закриваємо пул потоків після завершення роботи всіх потоків
            executeIt.shutdown();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}