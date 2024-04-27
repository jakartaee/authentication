
package ee.jakarta.tck.authentication.test.basic.soap;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ee.jakarta.tck.authentication.test.basic.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _SayHelloProtected_QNAME = new QName("http://soap.basic.test.authentication.tck.jakarta.ee/", "sayHelloProtected");
    private static final QName _SayHelloProtectedResponse_QNAME = new QName("http://soap.basic.test.authentication.tck.jakarta.ee/", "sayHelloProtectedResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ee.jakarta.tck.authentication.test.basic.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SayHelloProtected }
     * 
     * @return
     *     the new instance of {@link SayHelloProtected }
     */
    public SayHelloProtected createSayHelloProtected() {
        return new SayHelloProtected();
    }

    /**
     * Create an instance of {@link SayHelloProtectedResponse }
     * 
     * @return
     *     the new instance of {@link SayHelloProtectedResponse }
     */
    public SayHelloProtectedResponse createSayHelloProtectedResponse() {
        return new SayHelloProtectedResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHelloProtected }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SayHelloProtected }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.basic.test.authentication.tck.jakarta.ee/", name = "sayHelloProtected")
    public JAXBElement<SayHelloProtected> createSayHelloProtected(SayHelloProtected value) {
        return new JAXBElement<>(_SayHelloProtected_QNAME, SayHelloProtected.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHelloProtectedResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SayHelloProtectedResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soap.basic.test.authentication.tck.jakarta.ee/", name = "sayHelloProtectedResponse")
    public JAXBElement<SayHelloProtectedResponse> createSayHelloProtectedResponse(SayHelloProtectedResponse value) {
        return new JAXBElement<>(_SayHelloProtectedResponse_QNAME, SayHelloProtectedResponse.class, null, value);
    }

}
