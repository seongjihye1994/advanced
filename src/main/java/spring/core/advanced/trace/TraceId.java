package spring.core.advanced.trace;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TraceId {

    private String id; // 트랜잭션 id
    private int level; // 레벨

    // 커스텀 생성자
    public TraceId() {
        this.id = createId(); // 임의의 UUID로 생성한 숫자의 앞 8자리 id 생성
        this.level = 0;
    }

    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        // ab99e16f-3cde-4d24-8241-256108c203a2 -> 생성된 UUID
        // ab99e16f -> 앞 8자리만 사용
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // 다음 트레이스 id 생성해주는 메소드 -> 예제를 보면 트레이스 id는 동일하지만, 레벨은 +1씩 추가된다.
    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }

    // 이전 트레이스 id 생성해주는 메소드 -> 이전 레벨이니까 lelve
    public TraceId createPreviousId() {
        return new TraceId(id, level - 1);
    }

    // 첫번째 레벨인지 판단하는 메소드
    public boolean isFirstLevel() {
        return level == 0;
    }


}
