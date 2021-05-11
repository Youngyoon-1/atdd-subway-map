package wooteco.subway.exception;

public enum ExceptionStatus {
    ID_NOT_FOUND(400, "해당 ID로 조회할 수 없습니다."),
    DUPLICATED_NAME(400, "이름이 중복되었습니다."),
    INVALID_SECTION(400, "잘못된 구간 입니다."),
    SECTION_NOT_ADDABLE(400, "구간을 추가할 수 없습니다."),
    SECTION_NOT_DELETABLE(400, "구간을 삭제할 수 없습니다."),
    SECTION_NOT_CONNECTABLE(400, "구간을 연결할 수 없습니다.");

    private final int statusCode;
    private final String message;

    ExceptionStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
