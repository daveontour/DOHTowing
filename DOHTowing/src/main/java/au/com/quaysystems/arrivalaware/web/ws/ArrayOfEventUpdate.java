//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.10 at 08:48:04 PM GST 
//


package au.com.quaysystems.arrivalaware.web.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfEventUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfEventUpdate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EventUpdate" type="{http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes}EventUpdate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfEventUpdate", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes", propOrder = {
    "eventUpdate"
})
public class ArrayOfEventUpdate {

    @XmlElement(name = "EventUpdate", nillable = true)
    protected List<EventUpdate> eventUpdate;

    /**
     * Gets the value of the eventUpdate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventUpdate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventUpdate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventUpdate }
     * 
     * 
     */
    public List<EventUpdate> getEventUpdate() {
        if (eventUpdate == null) {
            eventUpdate = new ArrayList<EventUpdate>();
        }
        return this.eventUpdate;
    }

}