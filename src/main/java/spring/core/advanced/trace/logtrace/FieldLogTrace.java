package spring.core.advanced.trace.logtrace;

import lombok.extern.slf4j.Slf4j;
import spring.core.advanced.trace.TraceId;
import spring.core.advanced.trace.TraceStatus;

@Slf4j
public class FieldLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // traceId 동기화 (기존에는 파라미터로 넘겨줬지만, 지금은 보관해서 사용!) -> 동시성 이슈 발생함!
    private TraceId traceIdHolder;

    @Override
    public TraceStatus begin(String message) {
        syncTraceId(); // 로그 시작 시 호출
        TraceId traceId = traceIdHolder;
        Long startTimeMs = System.currentTimeMillis(); // 현재 시간
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    // 트레이스 아이디가 1 -> 2 -> 3...
    private void syncTraceId() {
        if (traceIdHolder == null) { // 트레이스 Id를 최초 호출이면 새로 만들고
            traceIdHolder = new TraceId();
        } else { // 아니면 다음값을 생성
            traceIdHolder = traceIdHolder.createNextId();
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
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

        releaseTraceId(); // 로그 종료 때 호출
    }

    // 트레이스 아이디가 3 -> 2 -> 1...
    private void releaseTraceId() {

        if (traceIdHolder.isFirstLevel()) { // 첫번째 레벨이면
            traceIdHolder = null; // destroy
        } else { // 첫번째 레벨이 아니면 이전 레벨 생성
            traceIdHolder = traceIdHolder.createPreviousId();
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
