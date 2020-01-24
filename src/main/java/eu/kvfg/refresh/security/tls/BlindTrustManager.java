package eu.kvfg.refresh.security.tls;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * Allows (blindly) every certificate.
 * Needed because the LDAP server uses a self-signed SSL certificate (nice job LogoDIDACT).
 */
public class BlindTrustManager extends X509ExtendedTrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {

    }
}
