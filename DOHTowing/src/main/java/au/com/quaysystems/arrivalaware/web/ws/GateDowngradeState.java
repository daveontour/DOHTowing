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
 * <p>Java class for GateDowngradeState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GateDowngradeState"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gatesField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfGateResource"/&gt;
 *         &lt;element name="valueField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfPropertyValue"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GateDowngradeState", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes", propOrder = {
    "gatesField",
    "valueField"
})
public class GateDowngradeState {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfGateResource gatesField;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfPropertyValue valueField;

    /**
     * Gets the value of the gatesField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfGateResource }
     *     
     */
    public ArrayOfGateResource getGatesField() {
        return gatesField;
    }

    /**
     * Sets the value of the gatesField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfGateResource }
     *     
     */
    public void setGatesField(ArrayOfGateResource value) {
        this.gatesField = value;
    }

    /**
     * Gets the value of the valueField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPropertyValue }
     *     
     */
    public ArrayOfPropertyValue getValueField() {
        return valueField;
    }

    /**
     * Sets the value of the valueField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPropertyValue }
     *     
     */
    public void setValueField(ArrayOfPropertyValue value) {
        this.valueField = value;
    }

}
