package wooteco.subway.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
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
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        var upStationId = sectionRequest.getUpStationId();
        var downStationId = sectionRequest.getDownStationId();
        var distance = sectionRequest.getDistance();

        var sections = new Sections(sectionDao.findByLineId(lineId));

        sectionDao.update(sections.createUpdatedSection(new Section(upStationId, downStationId, distance)));
        sectionDao.save(lineId, sectionRequest);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        var allSections = sectionDao.findByLineId(lineId);

        var sections = new Sections(allSections, stationId);

        if (sections.hasOnlyOneSection()) {
            sectionDao.delete(sections.getFirstSection());
            return;
        }

        sectionDao.update(new Section(sections));
        sectionDao.delete(sections.getSecondSection());
    }
}
