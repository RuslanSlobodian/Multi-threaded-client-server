import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonoThreadClientHandler implements Runnable {
    private static Socket clientDialog;

    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
    }

    @Override
    public void run() {
        try {

            /* ініціалізуємо потоки спілкування в сокеті,
            для сервера потік запису в сокет потрібно ініціалізувати скоріше за потік читання
            для уникнення блокування виконання програми при очікування заголовку в сокеті*/
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());

            // потік читання із сокета
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");
            System.out.println("DataOutputStream created");

            // починаємо діалог із підключеним клієнтом в циклі, поки сокет не буде закритим клієнтом
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");

                // серверний потік очікує на отримання вхідних повідомлень клієнта
                String entry = in.readUTF();

                // вивід повідомлення від клієнта в консоль
                System.out.println("READ from clientDialog message - " +
                        entry);
                // перевірка умови завершення з’єднання – присутність у повідомленні ключового слова - quit
                if (entry.equalsIgnoreCase("quit")) {

                    // якщо ключове слово присутнє, то ініціалізується закриття серверного потоку
                    // із виводом відповідного повідомлення у консоль + відповідь клієнту
                    System.out.println("Client initialize connections suicide ...");
                    out.writeUTF("Server reply - " + entry + " - OK");
                    Thread.sleep(3000);
                    break;
                }

                // відправка ехо-повідомлення клієнту
                System.out.println("Server try writing to channel");
                out.writeUTF("Server reply - " + entry + " - OK");
                System.out.println("Server Wrote message to clientDialog.");

                // звільняється буфер мережевих повідомлень
                out.flush();
                // повертаємось назад для зчитування нового повідомлення
            }

            // якщо умова виходу підтверджується – закриваємо з’єднання
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закриваємо потоки сокета!
            in.close();
            out.close();

            // потім закриваємо сокет повідомлень клієнтом в потоці
            clientDialog.close();
            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Зберігає статус перерваності потоку
        }
    }
}
