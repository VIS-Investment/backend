package vis.backend.demo.global.utils;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;

@Component
public class FetchRetry {
    public <T> T retry(int maxAttempts, long delayMillis, Callable<T> task, String ticker) throws Exception {
        int attempt = 0;
        while (true) {
            try {
                return task.call();
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxAttempts || isUnrecoverable(e)) {
                    throw e;
                }
                System.err.println("[" + ticker + "] Retrying... Attempt " + attempt + " due to: " + e.getMessage());
                Thread.sleep(delayMillis);
            }
        }
    }

    // 예외 메시지 기반으로 재시도 여부 결정
    private boolean isUnrecoverable(Exception e) {
        return e.getMessage() != null && e.getMessage().contains("No data found");
    }
}
