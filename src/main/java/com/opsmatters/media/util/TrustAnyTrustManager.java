/*
 * Copyright 2019 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.util;

import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HttpsURLConnection;

/**
 * A trust manager that trusts any certificates.
 * <p>
 * Used to access images from sites with certificate issues.
 *
 * @author Gerald Curley (opsmatters)
 */
public class TrustAnyTrustManager implements X509TrustManager
{
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }

    public static void setTrustManager(HttpURLConnection conn)
    {
        if(conn instanceof HttpsURLConnection)
        {
            try
            {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
                ((HttpsURLConnection)conn).setSSLSocketFactory(sc.getSocketFactory());
            }
            catch(GeneralSecurityException e)
            {
            }
        }
    }
}
