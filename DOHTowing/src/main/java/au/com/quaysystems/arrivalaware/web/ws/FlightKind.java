//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.10 at 08:48:04 PM GST 
//


package au.com.quaysystems.arrivalaware.web.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FlightKind.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FlightKind"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Arrival"/&gt;
 *     &lt;enumeration value="Departure"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FlightKind", namespace = "http://schemas.datacontract.org/2004/07/WorkBridge.Modules.AMS.AMSIntegrationAPI.Mod.Intf.DataTypes")
@XmlEnum
public enum FlightKind {

    @XmlEnumValue("Arrival")
    ARRIVAL("Arrival"),
    @XmlEnumValue("Departure")
    DEPARTURE("Departure");
    private final String value;

    FlightKind(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FlightKind fromValue(String v) {
        for (FlightKind c: FlightKind.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
