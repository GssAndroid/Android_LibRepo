package com.globalsoft.SapLibSoap.SoapConnection;

import java.io.*;

import org.ksoap2.*;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.*;

import android.util.Log;

/**
 * A J2SE based HttpTransport layer.
 */
public class HttpTransportSE extends Transport {

    /**
     * Creates instance of HttpTransportSE with set url
     * 
     * @param url
     *            the destination to POST SOAP data
     */
    public HttpTransportSE(String url) {
        super(url);
    }

    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     */
    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        if (soapAction == null)
            soapAction = "\"\"";
        Log.i("SAP", "vor createRequestData");
        byte[] requestData = createRequestData(envelope);
        Log.i("SAP", "nach createRequestData");
        debug = true;
        requestDump = debug ? new String(requestData) : null;

        responseDump = null;
        
        ServiceConnection connection = getServiceConnection();
//        connection.setRequestProperty("User-Agent", "kSOAP/2.0");
        connection.setRequestProperty("User-Agent", "Jakarta Commons-HttpClient/3.1");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("Connection", "close");
        connection.setRequestProperty("Content-Length", "" + requestData.length);
        connection.setRequestMethod("POST");
        connection.connect();
        OutputStream os = connection.openOutputStream();
        String requestDump = new String(requestData);
        Log.i("request", requestDump);        
        os.write(requestData, 0, requestData.length);
        os.flush();
        os.close();
        requestData = null;
        InputStream is;
        Log.i("SAP", "vor connect");
        try {
            connection.connect();
            is = connection.openInputStream();
        } catch (IOException e) {
            Log.i("SAP", "IOException");

        	is = connection.getErrorStream();
            if (is == null) {
                connection.disconnect();
                throw (e);
            }
        }
        Log.i("SAP", "nach connect");
        if (true) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1)
                    break;
                bos.write(buf, 0, rd);
            }
            bos.flush();
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new ByteArrayInputStream(buf);
        }
        Log.i("response", responseDump);

        Log.i("SAP", "vor parseResponse");
        parseResponse(envelope, is);
    	Log.i("SAP", "nach parseResponse");
    }

    protected ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionSE(url);
    }
}