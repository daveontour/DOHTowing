//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.10 at 08:48:04 PM GST 
//


package au.com.quaysystems.arrivalaware.web.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodeShareFlightIdentifier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodeShareFlightIdentifier"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="airlineDesignatorField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfLookupCode"/&gt;
 *         &lt;element name="flightNumberField" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeShareFlightIdentifier", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes", propOrder = {
    "airlineDesignatorField",
    "flightNumberField"
})
public class CodeShareFlightIdentifier {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfLookupCode airlineDesignatorField;
    @XmlElement(required = true, nillable = true)
    protected String flightNumberField;

    /**
     * Gets the value of the airlineDesignatorField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLookupCode }
     *     
     */
    public ArrayOfLookupCode getAirlineDesignatorField() {
        return airlineDesignatorField;
    }

    /**
     * Sets the value of the airlineDesignatorField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLookupCode }
     *     
     */
    public void setAirlineDesignatorField(ArrayOfLookupCode value) {
        this.airlineDesignatorField = value;
    }

    /**
     * Gets the value of the flightNumberField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlightNumberField() {
        return flightNumberField;
    }

    /**
     * Sets the value of the flightNumberField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlightNumberField(String value) {
        this.flightNumberField = value;
    }

}
