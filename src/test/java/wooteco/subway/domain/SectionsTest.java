package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        this.sections = new Sections(List.of(new Section(1L, 3L, 2)));
    }

    @Test
    @DisplayName("상행역이 같은경우")
    void createSection1() {
        var section = new Section(1L, 2L, 1);

        var expected = new Section(2L, 3L, 1);

        assertAll(
                () -> assertThat(sections.createSection(section).get().getUpStationId()).isEqualTo(
                        expected.getUpStationId()),
                () -> assertThat(sections.createSection(section).get().getDownStationId()).isEqualTo(
                        expected.getDownStationId()),
                () -> assertThat(sections.createSection(section).get().getDistance()).isEqualTo(expected.getDistance())
        );
    }

    @Test
    @DisplayName("하행역이 같은경우")
    void createSection2() {
        var section = new Section(2L, 3L, 1);

        var expected = new Section(1L, 2L, 1);

        assertAll(
                () -> assertThat(sections.createSection(section).get().getUpStationId()).isEqualTo(
                        expected.getUpStationId()),
                () -> assertThat(sections.createSection(section).get().getDownStationId()).isEqualTo(
                        expected.getDownStationId()),
                () -> assertThat(sections.createSection(section).get().getDistance()).isEqualTo(expected.getDistance())
        );
    }
}
