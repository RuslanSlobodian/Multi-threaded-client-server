import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // створюємо та запускаємо пул потоків із обмеженою кількістю потоків - 10
        ExecutorService exec = Executors.newFixedThreadPool(10);
        int j = 0;

        // запускаємо цикл в якому з паузою в 10 мілісекунд запускаються клієнти,
        // які відправлятимуть повідомлення серверу
        while (j < 10) {
            j++;
            exec.execute(new TestRunnableClientTester());
            Thread.sleep(10);
        }

        // закриваємо фабрику
        exec.shutdown();
    }
}
