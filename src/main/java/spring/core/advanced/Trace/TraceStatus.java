package spring.core.advanced.Trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TraceStatus { // traceId 상태를 나타내는 클래스

    private TraceId traceId; // 내부에 트랜잭션ID와 level을 가지고 있다
    private Long startTimeMs; // 로그 시작시간이다. 로그 종료시 이 시작 시간을 기준으로 시작~종료까지 전체 수행 시간을 구할 수 있다.
    private String message; // 시작시 사용한 메시지이다. 이후 로그 종료시에도 이 메시지를 사용해서 출력한다.

}
