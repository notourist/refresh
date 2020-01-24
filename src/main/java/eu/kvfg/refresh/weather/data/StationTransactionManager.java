package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.weather.exception.StationNotFoundException;
import eu.kvfg.refresh.weather.model.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Lukas Nasarek
 */
@Service
@Transactional(readOnly = true, timeout = 5)
public class StationTransactionManager {

    private final StationDao stationDao;

    @Autowired
    public StationTransactionManager(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> listStations() {
        return stationDao.listStations();
    }

    public Station stationById(long id) {
        Station station = stationDao.stationById(id);

        if (station == null) {
            throw new StationNotFoundException();
        }

        return station;
    }
}
