package spring.core.advanced.app.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.core.advanced.trace.TraceStatus;
import spring.core.advanced.trace.hellotrace.HelloTraceV1;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {

    private final OrderRepositoryV1 orderRepository;
    private final HelloTraceV1 trace;

    public void orderItem(String itemId) {

        TraceStatus status = null;

        try {
            status = trace.begin("OrderService.orderItem()");
            orderRepository.save(itemId);
            trace.end(status);
        } catch (Exception e) { // 예외가 터지면 예외를 잡기
            trace.exception(status, e);
            throw e;
            // 요구사항 - 예외를 꼭 다시 던져줘야 한다.
            // -> catch로 잡으면 정상 흐름 처리 되니까, 다시 던져줘야 오류가 발생함
        }

        orderRepository.save(itemId);
    }
}
