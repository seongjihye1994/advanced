package spring.core.advanced.app.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spring.core.advanced.trace.TraceId;
import spring.core.advanced.trace.TraceStatus;
import spring.core.advanced.trace.hellotrace.HelloTraceV2;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {

    private final HelloTraceV2 trace;

    public void save(TraceId traceId, String itemId) {

        TraceStatus status = null;

        try {
            status = trace.beginSync(traceId, "OrderRepository.save()");

            // 저장 로직
            if (itemId.equals("ex")) { // 아이디가 ex로 오면 예외를 터트림
                throw new IllegalStateException("예외 발생");
            }
            sleep(1000);

            trace.end(status);
        } catch (Exception e) { // 예외가 터지면 예외를 잡기
            trace.exception(status, e);
            throw e;
            // 요구사항 - 예외를 꼭 다시 던져줘야 한다.
            // -> catch로 잡으면 정상 흐름 처리 되니까, 다시 던져줘야 오류가 발생함
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
