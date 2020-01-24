package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Lukas Nasarek
 */
@Service
@Transactional(readOnly = true, timeout = 5)
public class WeatherTransactionManager {

    private final WeatherDao weatherDao;

    @Autowired
    public WeatherTransactionManager(WeatherDao weatherDao) {
        this.weatherDao = weatherDao;
    }

    public List<WeatherData> allDataById(long id) {
        return weatherDao.allDataById(id);
    }

    public List<WeatherData> limitedDataById(long id, long begin, long end, int count, int type) {
        return weatherDao.limitedDataById(id, begin, end, count, type);
    }

    public List<WeatherData> lastDataById(long id) {
        return weatherDao.lastDataById(id);
    }
}
