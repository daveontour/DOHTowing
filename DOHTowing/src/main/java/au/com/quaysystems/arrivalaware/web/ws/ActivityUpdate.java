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
 * <p>Java class for ActivityUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActivityUpdate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codeField" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="updateField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfPropertyValue"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivityUpdate", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes", propOrder = {
    "codeField",
    "updateField"
})
public class ActivityUpdate {

    @XmlElement(required = true, nillable = true)
    protected String codeField;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfPropertyValue updateField;

    /**
     * Gets the value of the codeField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeField() {
        return codeField;
    }

    /**
     * Sets the value of the codeField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeField(String value) {
        this.codeField = value;
    }

    /**
     * Gets the value of the updateField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPropertyValue }
     *     
     */
    public ArrayOfPropertyValue getUpdateField() {
        return updateField;
    }

    /**
     * Sets the value of the updateField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPropertyValue }
     *     
     */
    public void setUpdateField(ArrayOfPropertyValue value) {
        this.updateField = value;
    }

}
