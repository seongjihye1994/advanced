package spring.core.advanced.app.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.core.advanced.trace.TraceStatus;
import spring.core.advanced.trace.hellotrace.HelloTraceV1;

@RestController // Controller + ResponseBody
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    @GetMapping("/v1/request")
    public String request(String itemId) {

        TraceStatus status = null;

        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(itemId);
            trace.end(status);
        } catch (Exception e) { // 예외가 터지면 예외를 잡기
            trace.exception(status, e);
            throw e;
            // 요구사항 - 예외를 꼭 다시 던져줘야 한다.
            // -> catch로 잡으면 정상 흐름 처리 되니까, 다시 던져줘야 오류가 발생함
        }

        return "ok";
    }
}
