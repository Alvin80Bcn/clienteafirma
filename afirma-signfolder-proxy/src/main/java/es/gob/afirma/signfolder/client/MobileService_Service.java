
package es.gob.afirma.signfolder.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "MobileService", targetNamespace = "urn:juntadeandalucia:cice:pfirma:mobile:v2.0", wsdlLocation = "http://appint.map.es/portafirma/servicesv2/MobileService?wsdl")
public class MobileService_Service
    extends Service
{

    private final static URL MOBILESERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(es.gob.afirma.signfolder.client.MobileService_Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = es.gob.afirma.signfolder.client.MobileService_Service.class.getResource(".");
            url = new URL(baseUrl, "http://appint.map.es/portafirma/servicesv2/MobileService?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://appint.map.es/portafirma/servicesv2/MobileService?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        MOBILESERVICE_WSDL_LOCATION = url;
    }

    public MobileService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MobileService_Service() {
        super(MOBILESERVICE_WSDL_LOCATION, new QName("urn:juntadeandalucia:cice:pfirma:mobile:v2.0", "MobileService"));
    }

    /**
     * 
     * @return
     *     returns MobileService
     */
    @WebEndpoint(name = "MobileServicePort")
    public MobileService getMobileServicePort() {
        return super.getPort(new QName("urn:juntadeandalucia:cice:pfirma:mobile:v2.0", "MobileServicePort"), MobileService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MobileService
     */
    @WebEndpoint(name = "MobileServicePort")
    public MobileService getMobileServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:juntadeandalucia:cice:pfirma:mobile:v2.0", "MobileServicePort"), MobileService.class, features);
    }

}
