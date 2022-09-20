package spring.core.advanced.trace.hellotrace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.core.advanced.trace.TraceId;
import spring.core.advanced.trace.TraceStatus;

@Slf4j
@Component
public class HelloTraceV2 {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // trace 시작시 호출 -> [796bccd9] OrderController.request() // 로그 시작
    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        Long startTimeMs = System.currentTimeMillis(); // 현재 시간
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    // V2에서 추가
    public TraceStatus beginSync(TraceId beforeTraceId, String message) {
//        TraceId traceId = new TraceId();
        TraceId nextId = beforeTraceId.createNextId(); // id는 그대로 유지, level만 ++
        Long startTimeMs = System.currentTimeMillis(); // 현재 시간
        log.info("[{}] {}{}", nextId.getId(), addSpace(START_PREFIX, nextId.getLevel()), message);

        return new TraceStatus(nextId, startTimeMs, message);
    }

    // trace 끝나면 호출 -> [796bccd9] OrderController.request() time=1016ms // 로그 종료
    public void end(TraceStatus status) {
        complete(status, null); // trace 상태만 넘김
    }

    // Exception 터질 시 호출 -> 정상 종료와 예외 발생은 서로 다르게 출력해줘야 하기 때문에
    public void exception(TraceStatus status, Exception e) {
        complete(status, e); // trace 상태와 exception 넘김
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();

        // exception 이 없으면
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else { // exception 이 있으면
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString()); // 에러 출력
        }
    }

    // level 이 0이면 아무것도 x
    // level 이 1이면 |-->
    // level 이 2이면 |    |-->

    // level 이 1이면서 exception 이 있으면 |<X-
    // level 이 2이면서 exception 이 있으면 |   |<X- ...
    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }

        return sb.toString();
    }
}
