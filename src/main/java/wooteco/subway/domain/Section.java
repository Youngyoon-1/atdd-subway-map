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

    public static Section createWhenSameUpStation(Section target, Section subSection) {
        var distance = target.getDistance() - subSection.getDistance();
        return new Section(
                target.getId(),
                subSection.getDownStationId(),
                target.getDownStationId(),
                distance
        );
    }

    public static Section createWhenSameDownStation(Section target, Section subSection) {
        var distance = target.getDistance() - subSection.getDistance();
        return new Section(
                target.getId(),
                target.getUpStationId(),
                subSection.getUpStationId(),
                distance
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
}
