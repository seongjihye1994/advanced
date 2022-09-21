package spring.core.advanced.app.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.core.advanced.trace.TraceId;
import spring.core.advanced.trace.TraceStatus;
import spring.core.advanced.trace.hellotrace.HelloTraceV2;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

    private final OrderRepositoryV2 orderRepository;
    private final HelloTraceV2 trace;

    public void orderItem(TraceId traceId, String itemId) {

        TraceStatus status = null;

        try {
            status = trace.beginSync(traceId, "OrderService.orderItem()");
            orderRepository.save(status.getTraceId(), itemId);
            trace.end(status);
        } catch (Exception e) { // 예외가 터지면 예외를 잡기
            trace.exception(status, e);
            throw e;
            // 요구사항 - 예외를 꼭 다시 던져줘야 한다.
            // -> catch로 잡으면 정상 흐름 처리 되니까, 다시 던져줘야 오류가 발생함
        }
    }
}
