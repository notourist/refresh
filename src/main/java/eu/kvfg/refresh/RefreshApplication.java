package eu.kvfg.refresh;

import eu.kvfg.refresh.security.tls.BlindTrustManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;

/**
 * Application entry point.
 *
 * @author Lukas Nasarek
 */
@SpringBootApplication
public class RefreshApplication {

    public static void main(String... args) {
        disableSslVerification();
        SpringApplication.run(RefreshApplication.class, args);
    }

    /**
     * Overrides the default {@link SSLContext}.
     *
     * @see BlindTrustManager
     */
    private static void disableSslVerification() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new BlindTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}