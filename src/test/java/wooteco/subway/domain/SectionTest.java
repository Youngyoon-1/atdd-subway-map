//package wooteco.subway.domain;
//
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//public class SectionTest {
//
//    private Section section;
//
//    @BeforeEach
//    void setUp() {
//        section = new Section(1L, 3L, 1);
//    }
//
//    @Test
//    @DisplayName("기존 역 사이 길이보다 큰 경우 예외발생")
//    void checkDistance1() {
//        var section = new Section();
//        Section.createWhenSameUpStation();
//        Section.createWhenSameDownStation();
//    }
//
//    @Test
//    @DisplayName("기존 역 사이 길이가 같은 경우 예외발생")
//    void checkDistance2() {
//        Section.createWhenSameUpStation();
//        Section.createWhenSameDownStation();
//    }
//}
