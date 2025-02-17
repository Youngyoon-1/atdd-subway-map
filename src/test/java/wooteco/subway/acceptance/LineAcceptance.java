package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptance extends AcceptanceTest {

    private ExtractableResponse<Response> responseCreateLine;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        responseCreateLine = insertLine("테스트호선", "테스트색");
    }

    private ExtractableResponse<Response> insertLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return requestCreateLine(params);
    }

    private ExtractableResponse<Response> requestCreateLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("새로운 노선 생성한다.")
    @Test
    void createLine() {
        assertAll(
                () -> assertThat(responseCreateLine.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(responseCreateLine.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 생성시 예외가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        var response = insertLine("테스트호선", "테스트색");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void showLine() {
        //given
        var id = getId(responseCreateLine);

        // when
        var response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(id),
                () -> assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("테스트호선"),
                () -> assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("테스트색")
        );
    }

    private String getId(ExtractableResponse<Response> response) {
        return response.header("Location").split("/")[2];
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        var responseCreateLine2 = insertLine("테스트2호선", "테스트2색");

        // when
        var response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        var ids = getIds(response);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(ids).contains(getId(responseCreateLine)),
                () -> assertThat(ids).contains(getId(responseCreateLine2))
        );
    }

    private List<String> getIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @DisplayName("노선을 업데이트한다.")
    @Test
    void updateLine() {
        /// given
        var id = getId(responseCreateLine);

        // when
        var response = requestUpdateLine(id, "테스트2호선", "테스트색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> requestUpdateLine(String id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    @DisplayName("다른 노선이 가지고있는 정보로 현재 노선을 업데이트시 400에러 발생")
    @Test
    void failUpdateLine() {
        /// given
        var firstInsertId = getId(responseCreateLine);

        insertLine("테스트2호선", "테스트2색");

        // when
        var response = requestUpdateLine(firstInsertId, "테스트2호선", "테스트색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존 노선을 삭제한다.")
    @Test
    void deleteLine() {
        /// given
        var id = getId(responseCreateLine);

        // when
        var response = requestDeleteLine(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestDeleteLine(String id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    @DisplayName("존재하지 않는 노션을 삭제하려는 경우 400에러 발생")
    @Test
    void invalidLine() {
        /// given
        var invalidId = "-1";

        // when
        var response = requestDeleteLine(invalidId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}

