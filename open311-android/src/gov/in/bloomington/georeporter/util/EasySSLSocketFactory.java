/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package gov.in.bloomington.georeporter.util;

import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeLayeredSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

/**
 * This socket factory will create ssl socket that accepts self signed certificate
 *
 * @author olamy
 * @version $Id$
 * @since 1.2.3
 */
public class EasySSLSocketFactory implements SchemeSocketFactory, SchemeLayeredSocketFactory
{
    private SSLContext sslcontext = null;

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance( "SSL" );
            context.init( null, new TrustManager[]{new EasyX509TrustManager( null )}, null );
            return context;
        }
        catch (Exception e) {
            throw new IOException( e.getMessage() );
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    /**
     * @see ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory#connectSocket(java.net.Socket, java.net.InetSocketAddress, java.net.InetSocketAddress, ch.boye.httpclientandroidlib.params.HttpParams)
     */
    @Override
    public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout( params );
        int soTimeout   = HttpConnectionParams.getSoTimeout( params );

        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket(params));

        // we need to bind explicitly
        if (localAddress != null) {
            sslsock.bind(localAddress);
        }

        sslsock.connect( remoteAddress, connTimeout );
        sslsock.setSoTimeout( soTimeout );
        return sslsock;
    }

    /**
     * @see ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory#createSocket(ch.boye.httpclientandroidlib.params.HttpParams)
     */
    @Override
    public Socket createSocket(HttpParams params) throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    /**
     * @see ch.boye.httpclientandroidlib.conn.scheme.SocketFactory#isSecure(java.net.Socket)
     */
    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }

    /**
     * @see ch.boye.httpclientandroidlib.conn.scheme.SchemeLayeredSocketFactory#createLayeredSocket(java.net.Socket, java.lang.String, int, ch.boye.httpclientandroidlib.params.HttpParams)
     */
    @Override
    public Socket createLayeredSocket(Socket socket, String host, int port, HttpParams params) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    // -------------------------------------------------------------------
    //  javadoc in ch.boye.httpclientandroidlib.conn.scheme.SocketFactory says :
    //  Both Object.equals() and Object.hashCode() must be overridden 
    //  for the correct operation of some connection managers
    // -------------------------------------------------------------------
    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));
    }

    public int hashCode() {
        return EasySSLSocketFactory.class.hashCode();
    }
}
