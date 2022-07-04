//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0-M3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.07.02 at 08:06:39 PM CEST 
//


package com.sun.ts.lib.implementation.sun.javaee.runtime.ejb;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pmIdentifier",
    "pmVersion",
    "pmConfig",
    "pmClassGenerator",
    "pmMappingFactory"
})
@XmlRootElement(name = "pm-descriptor")
public class PmDescriptor
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "pm-identifier", required = true)
    protected String pmIdentifier;
    @XmlElement(name = "pm-version", required = true)
    protected String pmVersion;
    @XmlElement(name = "pm-config")
    protected String pmConfig;
    @XmlElement(name = "pm-class-generator")
    protected String pmClassGenerator;
    @XmlElement(name = "pm-mapping-factory")
    protected String pmMappingFactory;

    /**
     * Gets the value of the pmIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmIdentifier() {
        return pmIdentifier;
    }

    /**
     * Sets the value of the pmIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmIdentifier(String value) {
        this.pmIdentifier = value;
    }

    /**
     * Gets the value of the pmVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmVersion() {
        return pmVersion;
    }

    /**
     * Sets the value of the pmVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmVersion(String value) {
        this.pmVersion = value;
    }

    /**
     * Gets the value of the pmConfig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmConfig() {
        return pmConfig;
    }

    /**
     * Sets the value of the pmConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmConfig(String value) {
        this.pmConfig = value;
    }

    /**
     * Gets the value of the pmClassGenerator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmClassGenerator() {
        return pmClassGenerator;
    }

    /**
     * Sets the value of the pmClassGenerator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmClassGenerator(String value) {
        this.pmClassGenerator = value;
    }

    /**
     * Gets the value of the pmMappingFactory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmMappingFactory() {
        return pmMappingFactory;
    }

    /**
     * Sets the value of the pmMappingFactory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmMappingFactory(String value) {
        this.pmMappingFactory = value;
    }

}
