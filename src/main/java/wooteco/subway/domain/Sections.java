package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Optional<Section> createSection(Section section) {
        var sameUpStationSection = sections.stream().filter(it -> it.isSameUpStationId(section)).findAny();
        var downUpStationSection = sections.stream().filter(it -> it.isSameDownStationId(section)).findAny();

        if (sameUpStationSection.isPresent() && downUpStationSection.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        return sameUpStationSection.map(it -> Section.createWhenSameUpStation(it, section))
                .or(() -> downUpStationSection.map(it -> Section.createWhenSameDownStation(it, section)));
    }
}
