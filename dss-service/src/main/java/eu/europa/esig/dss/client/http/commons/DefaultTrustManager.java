package eu.europa.esig.dss.client.http.commons;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;

/**
 * Default trust manager.
 * 
 * @author lodermatt
 */
public final class DefaultTrustManager implements X509TrustManager {

    /** TrustStore. */
    private X509TrustManager trustManager;

    /**
     * @param keystore
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    public DefaultTrustManager(final KeyStore keystore) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        super();
        // initialize a new TMF with the ts we just loaded
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);

        // acquire X509 trust manager from factory
        final TrustManager[] tms = tmf.getTrustManagers();

        for (final TrustManager tm : tms) {
            if (tm instanceof X509TrustManager) {
                this.trustManager = (X509TrustManager) tm;
                return;
            }
        }
        throw new NoSuchAlgorithmException("No X509TrustManager in TrustManagerFactory");
    }

    /**
     * Constructor.
     * 
     * @param tsInputStream The truststore
     * @param tsPasswd truststore password
     * @throws KeyStoreException Keystore error
     * @throws NoSuchAlgorithmException Algorithm not found
     * @throws CertificateException Certificate error
     * @throws IOException I/O Error
     */
    public DefaultTrustManager(final InputStream tsInputStream, final String tsPasswd) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        super();

        // load keystore from specified cert store (or default)
        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(tsInputStream, StringUtils.trimToEmpty(tsPasswd).toCharArray());
        this.initTrustManager(keystore);
    }

    /**
     * Loading the truststore.
     * 
     * @param keystore truststore
     * @throws KeyStoreException error
     * @throws KeyStoreException Keystore error
     * @throws NoSuchAlgorithmException Algorithm not found
     * @throws CertificateException Certificate error
     * @throws IOException I/O Error
     */
    private void initTrustManager(final KeyStore keystore) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException { // NOPMD

        // initialize a new TMF with the ts we just loaded
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keystore);

        // acquire X509 trust manager from factory
        final TrustManager[] tms = tmf.getTrustManagers();

        for (final TrustManager tm : tms) {
            if (tm instanceof X509TrustManager) {
                this.trustManager = (X509TrustManager) tm;
                return;
            }
        }

        throw new NoSuchAlgorithmException("No X509TrustManager in TrustManagerFactory"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.trustManager.checkClientTrusted(chain, authType);
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.trustManager.checkServerTrusted(chain, authType);
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.trustManager.getAcceptedIssuers();
    }

}
