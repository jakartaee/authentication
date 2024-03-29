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
    "methodName",
    "queryParams",
    "queryFilter",
    "queryVariables",
    "queryOrdering"
})
@XmlRootElement(name = "finder")
public class Finder
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "method-name", required = true)
    protected String methodName;
    @XmlElement(name = "query-params")
    protected String queryParams;
    @XmlElement(name = "query-filter")
    protected String queryFilter;
    @XmlElement(name = "query-variables")
    protected String queryVariables;
    @XmlElement(name = "query-ordering")
    protected String queryOrdering;

    /**
     * Gets the value of the methodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the value of the methodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodName(String value) {
        this.methodName = value;
    }

    /**
     * Gets the value of the queryParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryParams() {
        return queryParams;
    }

    /**
     * Sets the value of the queryParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryParams(String value) {
        this.queryParams = value;
    }

    /**
     * Gets the value of the queryFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryFilter() {
        return queryFilter;
    }

    /**
     * Sets the value of the queryFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryFilter(String value) {
        this.queryFilter = value;
    }

    /**
     * Gets the value of the queryVariables property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryVariables() {
        return queryVariables;
    }

    /**
     * Sets the value of the queryVariables property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryVariables(String value) {
        this.queryVariables = value;
    }

    /**
     * Gets the value of the queryOrdering property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryOrdering() {
        return queryOrdering;
    }

    /**
     * Sets the value of the queryOrdering property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryOrdering(String value) {
        this.queryOrdering = value;
    }

}
