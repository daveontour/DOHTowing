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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FlightUpdateInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightUpdateInformation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="activityUpdateField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfActivityUpdate"/&gt;
 *         &lt;element name="eventUpdateField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfEventUpdate"/&gt;
 *         &lt;element name="tableValueUpdateField" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}ArrayOfTableValueUpdate"/&gt;
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
@XmlType(name = "FlightUpdateInformation", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes", propOrder = {
    "activityUpdateField",
    "eventUpdateField",
    "tableValueUpdateField",
    "updateField"
})
@XmlSeeAlso({
    FlightUpdateInformationExtended.class
})
public class FlightUpdateInformation {

    @XmlElement(required = true, nillable = true)
    protected ArrayOfActivityUpdate activityUpdateField;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfEventUpdate eventUpdateField;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfTableValueUpdate tableValueUpdateField;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfPropertyValue updateField;

    /**
     * Gets the value of the activityUpdateField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfActivityUpdate }
     *     
     */
    public ArrayOfActivityUpdate getActivityUpdateField() {
        return activityUpdateField;
    }

    /**
     * Sets the value of the activityUpdateField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfActivityUpdate }
     *     
     */
    public void setActivityUpdateField(ArrayOfActivityUpdate value) {
        this.activityUpdateField = value;
    }

    /**
     * Gets the value of the eventUpdateField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfEventUpdate }
     *     
     */
    public ArrayOfEventUpdate getEventUpdateField() {
        return eventUpdateField;
    }

    /**
     * Sets the value of the eventUpdateField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfEventUpdate }
     *     
     */
    public void setEventUpdateField(ArrayOfEventUpdate value) {
        this.eventUpdateField = value;
    }

    /**
     * Gets the value of the tableValueUpdateField property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTableValueUpdate }
     *     
     */
    public ArrayOfTableValueUpdate getTableValueUpdateField() {
        return tableValueUpdateField;
    }

    /**
     * Sets the value of the tableValueUpdateField property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTableValueUpdate }
     *     
     */
    public void setTableValueUpdateField(ArrayOfTableValueUpdate value) {
        this.tableValueUpdateField = value;
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
