package wooteco.subway.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse findLineInfos(Long id) {
        var sections = sectionDao.findByLineId(id);

        var stations = sections.stream()
                .map(it -> stationDao.findById(it.getUpStationId(), it.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        var line = lineDao.findById(id);

        return new LineResponse(line, stations);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(LineResponse::getId)
                .map(this::findLineInfos)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        var upStationId = lineRequest.getUpStationId();
        var downStationId = lineRequest.getDownStationId();

        var line = lineDao.save(lineRequest);

        var stations = List.of(stationDao.findById(upStationId), stationDao.findById(downStationId));

        sectionDao.save(line.getId(), new SectionRequest(upStationId, downStationId, lineRequest.getDistance()));

        return new LineResponse(line, stations);
    }

    public void updateById(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    @Transactional
    public void createSection(Long lineId, SectionRequest sectionRequest) {
        //하행 또는 상행이 중복역인 경우 기존 구간을 변경한다.
        var upStationId = sectionRequest.getUpStationId();
        var downStationId = sectionRequest.getDownStationId();

        var sections = sectionDao.findByLineId(lineId);

        // 상행역이 같은 경우
        if (sections.stream().anyMatch(it -> it.getUpStationId().equals(upStationId))) {
            var section = sections.stream().filter(it -> it.getUpStationId().equals(upStationId)).findAny().get();
            var distance = section.getDistance() - sectionRequest.getDistance();
            var newSectionRequest = new SectionRequest(
                    section.getId(),
                    sectionRequest.getDownStationId(),
                    section.getDownStationId(),
                    distance
            );
            sectionDao.update(newSectionRequest);
            sectionDao.save(lineId, sectionRequest);
            return;
        }

        // 하행역이 같은 경우
        if (sections.stream().anyMatch(it -> it.getDownStationId().equals(downStationId))) {
            var section = sections.stream().filter(it -> it.getDownStationId().equals(downStationId)).findAny().get();
            var distance = section.getDistance() - sectionRequest.getDistance();
            var newSectionRequest = new SectionRequest(
                    section.getId(),
                    section.getUpStationId(),
                    upStationId,
                    distance
            );
            sectionDao.update(newSectionRequest);
            sectionDao.save(lineId, sectionRequest);
            return;
        }

        sectionDao.save(lineId, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        sectionDao.delete(lineId, stationId);
    }
}
