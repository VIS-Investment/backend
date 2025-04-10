package vis.backend.demo.global.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class FetchRetryTest {

    private final FetchRetry fetchRetry = new FetchRetry();

    @Test
    void 성공적으로_첫_시도에_성공하면_재시도_없이_종료된다() throws Exception {
        String result = fetchRetry.retry(3, 100, () -> "success", "AAPL");

        assertEquals("success", result);
    }

    @Test
    void 실패하다가_재시도_끝에_성공하면_정상_리턴된다() throws Exception {
        AtomicInteger attempt = new AtomicInteger(0);

        String result = fetchRetry.retry(3, 100, () -> {
            if (attempt.incrementAndGet() < 2) {
                throw new RuntimeException("Temporary error");
            }
            return "recovered";
        }, "TSLA");

        assertEquals("recovered", result);
        assertEquals(2, attempt.get());
    }

    @Test
    void 최대_재시도_횟수를_초과하면_예외를_던진다() {
        AtomicInteger attempt = new AtomicInteger(0);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fetchRetry.retry(2, 100, () -> {
                attempt.incrementAndGet();
                throw new RuntimeException("Keep failing");
            }, "MSFT");
        });

        assertTrue(exception.getMessage().contains("Keep failing"));
        assertEquals(2, attempt.get());
    }

    @Test
    void 복구불가능한_예외는_재시도_하지_않고_즉시_실패한다() {
        AtomicInteger attempt = new AtomicInteger(0);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fetchRetry.retry(3, 100, () -> {
                attempt.incrementAndGet();
                throw new RuntimeException("No data found for symbol");
            }, "GOOG");
        });

        assertTrue(exception.getMessage().contains("No data found"));
        assertEquals(1, attempt.get()); // 재시도 안 함
    }
}
