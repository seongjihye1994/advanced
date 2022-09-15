package spring.core.advanced.trace.hellotrace;

import org.junit.jupiter.api.Test;
import spring.core.advanced.trace.TraceStatus;

class HelloTraceV1Test {

    @Test
    void begin_end() {
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.end(status); // 정상 종료
    }

    @Test
    void begin_exception() {
        HelloTraceV1 trace = new HelloTraceV1();
        TraceStatus status = trace.begin("hello");
        trace.exception(status, new IllegalStateException()); // exception 종료
    }

}