package nl.thehyve.ocdu.soap.ResponseHandlers;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by Jacob Rousseau on 16-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class ImportDataResponseHandler extends OCResponseHandler {

    /**
     * Checks if an error occurred on the OpenClinica-side and reports it back as the
     * return value
     *
     * @param response the SOAP-response.
     * @return a non <code>null</code> error code.message if an error occurred. Some are reported by the OpenClinica-WS
     * instance at url. Returns <code>null</code> if everything went OK.
     * @throws Exception if a technical error occurs.
     */
    public static String parseImportDataResponse(SOAPMessage response) throws Exception {
        return parseGenericResponse(response, ".//importDataResponse");
    }

}
