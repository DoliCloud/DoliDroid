package com.nltechno.utils;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;

/**
 * Class to accept SSL 
 *  
 * @author ldestailleur
 */

/* Disabled, no more used

public class MySSLSocketFactory extends SSLSocketFactory {
    private static final String LOG_TAG = "DoliDroidMySSLSockerFactory";

    SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super();

        Log.d(LOG_TAG, "MySSLSocketFactory");

        TrustManager tm = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                Log.w(LOG_TAG, "MySSLSocketFactory Test server certificate");
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                Log.w(LOG_TAG, "MySSLSocketFactory checkClientTrusted");
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) throws CertificateException {
                // checkServerTrusted
                Log.w(LOG_TAG, "MySSLSocketFactory checkClientTrusted");

                if (certs == null || certs.length == 0) {
                    throw new CertificateException("Certificate chain is null or empty");
                }

                // Example: Check if the certificate is expired or not yet valid
                for (X509Certificate cert : certs) {
                    cert.checkValidity(); // This will throw a CertificateException if the certificate is not valid
                }

                if (true) {
                    throw new CertificateException("aaa");
                }

                // Delegate to the default trust manager for standard checks
                checkServerTrusted(certs, authType);
            }
        };

        sslContext.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
    }


    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }

}

*/
