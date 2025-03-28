package vis.backend.demo.stock.controller;

import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vis.backend.demo.stock.service.StockInsertService;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockPricesController {

    private final StockInsertService stockInsertService;

    /**
     * 미국 시장 종료 시간 (미국 동부 시간 기준 16:00)은 한국 시간 기준으로 다음 날 오전 6:00 (서머타임 미적용 시) 서머타임 적용 시 오전 5:00이 될 수 있음 따라서 스케줄링은 한국 시간
     * 기준 오전 6:00으로 설정 (서머타임 미반영 기준)
     */
    @Scheduled(cron = "0 10 6 * * TUE-SAT", zone = "Asia/Seoul")
    public void scheduledFetchAndInsert() {
        System.out.println("\uD83D\uDD52 Fetching stock prices at " + LocalTime.now(ZoneId.of("Asia/Seoul")));
        stockInsertService.fetchAndInsert("1d");
    }

    /**
     * 수동 호출용 엔드포인트
     */
    @PostMapping("/pre-insert")
    public void fetchNow(@RequestParam(name = "range", defaultValue = "1d") String range) {
        stockInsertService.fetchAndInsert(range);
    }

}
