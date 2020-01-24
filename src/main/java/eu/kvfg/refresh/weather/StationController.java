package eu.kvfg.refresh.weather;

import eu.kvfg.refresh.weather.data.StationTransactionManager;
import eu.kvfg.refresh.weather.data.WeatherTransactionManager;
import eu.kvfg.refresh.weather.exception.ImpossibleIntervalException;
import eu.kvfg.refresh.weather.exception.StationNotFoundException;
import eu.kvfg.refresh.weather.exception.TypeNotFoundException;
import eu.kvfg.refresh.weather.model.Station;
import eu.kvfg.refresh.weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/station")
public class StationController {

    private final StationTransactionManager stationTransactionManager;

    private final WeatherTransactionManager weatherTransactionManager;

    @Autowired
    public StationController(StationTransactionManager stationTransactionManager,
                             WeatherTransactionManager weatherTransactionManager) {
        this.stationTransactionManager = stationTransactionManager;
        this.weatherTransactionManager = weatherTransactionManager;
    }

    /**
     * Returns all stations from the database.
     *
     * @return every station
     */
    @GetMapping("/all")
    public List<Station> stations() {
        return stationTransactionManager.listStations();
    }

    /**
     * Returns a station based on the given id else a exception is thrown.
     *
     * @return the station with the id
     * @throws StationNotFoundException -
     */
    @GetMapping("/{id}")
    public List<Station> stationById(@PathVariable long id) {
        Station station = stationTransactionManager.stationById(id);

        return Collections.singletonList(station);
    }

    /**
     * Returns every saved data for the given id, if no data is found an exception is thrown.
     *
     * @return all data from a station
     * @throws StationNotFoundException -
     */
    @GetMapping("/{id}/all")
    public List<WeatherData> allDataById(@PathVariable long id) {
        Station station = stationTransactionManager.stationById(id);

        return weatherTransactionManager.allDataById(station.getId());
    }

    /**
     * Returns the saved data, which is between the given start and end time,
     * for the given id, if no data is found/the interval is not possible an exception is thrown.
     *
     * @return the specific data in a time interval
     * @throws StationNotFoundException -
     * @throws ImpossibleIntervalException -
     * @throws TypeNotFoundException -
     */
    @GetMapping("/{id}/limited")
    public List<WeatherData> limitedDataById(@PathVariable long id,
                                             @RequestParam(value = "begin") long begin,
                                             @RequestParam(value = "end", required = false,
                                                 defaultValue = "-1") long end,
                                             @RequestParam(value = "count", required = false,
                                                 defaultValue = "-1") int count,
                                             @RequestParam(value = "type") int type) {
        Station station = stationTransactionManager.stationById(id);

        if (end != -1 && begin > end) {
            throw new ImpossibleIntervalException();
        }

        if (type < 1 || type > 3) {
            throw new TypeNotFoundException();
        }

        count = (count != -1) ? count : 250;
        end = (end != -1) ? end : System.currentTimeMillis();

        return weatherTransactionManager.limitedDataById(station.getId(), begin, end, count, type);
    }

    /**
     * Returns the last send data by the station.
     *
     * @return the last send data
     * @throws StationNotFoundException -
     */
    @GetMapping("/{id}/last")
    public List<WeatherData> lastDataById(@PathVariable long id) {
        Station station = stationTransactionManager.stationById(id);

        return weatherTransactionManager.lastDataById(station.getId());
    }
}
