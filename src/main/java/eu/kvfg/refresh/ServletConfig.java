package eu.kvfg.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Lukas Nasarek
 */
@Configuration
public class ServletConfig implements WebMvcConfigurer {

    @Value("${spring.session.cookie.name}")
    private String cookieName;

    @Value("${spring.session.cookie.max-age}")
    private int cookieMaxAge;

    @Value("${spring.session.cookie.secure}")
    private boolean cookieSecure;

    @Value("${spring.session.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${spring.session.cookie.path}")
    private String cookiePath;

    /**
     * Disables the additional trailing slash Spring adds to every mapping.
     * This is ugly to configure with security.
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
            .setUseSuffixPatternMatch(false)
            .setUseTrailingSlashMatch(false);
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(cookieName);
        serializer.setCookiePath(cookiePath);
        serializer.setUseSecureCookie(cookieSecure);
        serializer.setUseHttpOnlyCookie(cookieHttpOnly);
        serializer.setCookieMaxAge(cookieMaxAge);
        return serializer;
    }
}