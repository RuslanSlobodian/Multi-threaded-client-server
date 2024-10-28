import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TestRunnableClientTester implements Runnable {
    static Socket socket;

    public TestRunnableClientTester() {
        try {
            // створюємо сокет спілкування на стороні клієнта
            socket = new Socket("localhost", 3345);
            System.out.println("Client connected to socket");
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (   // створюємо потік для запису рядків в створений сокет, потік
                // для читання із сокету в try-with-resources стилі
                DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
                DataInputStream ois = new DataInputStream(socket.getInputStream())) {

            System.out.println("Client oos & ois initialized");
            int i = 0;
            // створюємо робочий цикл
            while (i < 5) {
                // записуємо згенероване повідомлення в потік сокету для сервера
                oos.writeUTF("clientCommand " + i);
                // проштовхуємо повідомлення із буферу у потік
                oos.flush();
                // очікуємо певний час, щоб сервер встиг прочитати повідомлення із сокету і відповісти
                Thread.sleep(10);
                System.out.println("Client wrote & start waiting for data from server...");
                // забираємо відповідь із потоку, зберігаємо у стрічку та виводимо її вміст в консоль
                System.out.println("reading...");
                String in = ois.readUTF();
                System.out.println(in);
                i++;
                Thread.sleep(5000);
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Зберігає статус перерваності потоку
        }
    }
}
