package wooteco.subway.domain;

public class Section {
    private Long id;
    private final Long downStationId;
    private final Long upStationId;
    private final int distance;

    public Section(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Sections sections) {
        var firstSection = sections.getFirstSection();
        var secondSection = sections.getSecondSection();

        this.id = firstSection.getId();
        this.upStationId = firstSection.getUpStationId();
        this.downStationId = secondSection.getDownStationId();
        this.distance = firstSection.plusDistance(secondSection);
    }

    private int plusDistance(Section secondSection) {
        return distance + secondSection.getDistance();
    }

    public static Section createWhenSameUpStation(Section target, Section subSection) {
        return new Section(
                target.getId(),
                subSection.getDownStationId(),
                target.getDownStationId(),
                target.minusDistance(subSection)
        );
    }

    private int minusDistance(Section subSection) {
        var distance = this.distance - subSection.getDistance();

        if (distance > 0) {
            return distance;
        }

        throw new IllegalArgumentException("[ERROR] 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    public static Section createWhenSameDownStation(Section target, Section subSection) {
        return new Section(
                target.getId(),
                target.getUpStationId(),
                subSection.getUpStationId(),
                target.minusDistance(subSection)
        );
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getId() {
        return id;
    }

    public boolean isSameUpStationId(Section section) {
        return this.upStationId.equals(section.upStationId);
    }

    public boolean isSameDownStationId(Section section) {
        return this.downStationId.equals(section.downStationId);
    }

    public boolean isSameStationId(Long stationId) {
        return downStationId.equals(stationId) || upStationId.equals(stationId);
    }
}
