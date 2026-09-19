package yjh.ontongsal.common.domain.exception;

public enum HttpClientErrorType {

    CONNECT_TIMEOUT("TCP 연결 수립 실패. 서버가 응답하지 않거나 네트워크 문제."),
    READ_TIMEOUT("연결 후 응답 대기 시간 초과. 서버가 처리 중일 수 있음."),
    POOL_TIMEOUT("커넥션 풀 고갈로 대기 시간 초과. pool-size 또는 connection-request-timeout 조정 필요."),
    IO_ERROR("타임아웃 외 IO 오류. Connection reset, Connection refused, DNS 실패 등."),
    HTTP_4XX("상대 서버가 4xx 응답 반환. 잘못된 요청 또는 인증 실패."),
    HTTP_5XX("상대 서버가 5xx 응답 반환. 상대 서버 내부 오류."),
    UNKNOWN("알 수 없는 오류.");

    private final String description;

    HttpClientErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
