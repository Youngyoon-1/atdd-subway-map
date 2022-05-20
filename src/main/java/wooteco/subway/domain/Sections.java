package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections(List<Section> sections, Long stationId) {
        this.sections = sections.stream()
                .filter(it -> it.isSameStationId(stationId))
                .collect(Collectors.toList());
    }

    public Section createUpdatedSection(Section section) {
        var sameUpStationSection = sections.stream().filter(it -> it.isSameUpStationId(section)).findAny();
        var downUpStationSection = sections.stream().filter(it -> it.isSameDownStationId(section)).findAny();

        checkValidation(sameUpStationSection, downUpStationSection);

        return sameUpStationSection.map(it -> Section.createWhenSameUpStation(it, section))
                .or(() -> downUpStationSection.map(it -> Section.createWhenSameDownStation(it, section))).get();
    }

    private void checkValidation(Optional<Section> sameUpStationSection, Optional<Section> downUpStationSection) {
        if (sameUpStationSection.isPresent() && downUpStationSection.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (sameUpStationSection.isEmpty() && downUpStationSection.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
        }
    }

    public boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    public Section getSecondSection() {
        return sections.get(1);
    }
}
