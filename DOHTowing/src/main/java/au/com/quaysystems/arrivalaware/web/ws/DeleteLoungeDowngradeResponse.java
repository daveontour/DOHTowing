//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.10 at 08:48:04 PM GST 
//


package au.com.quaysystems.arrivalaware.web.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DeleteLoungeDowngradeResult" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;any processContents='lax' minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "deleteLoungeDowngradeResult"
})
@XmlRootElement(name = "DeleteLoungeDowngradeResponse")
public class DeleteLoungeDowngradeResponse {

    @XmlElementRef(name = "DeleteLoungeDowngradeResult", namespace = "http://www.sita.aero/ams6-xml-api-webservice", type = JAXBElement.class, required = false)
    protected JAXBElement<DeleteLoungeDowngradeResponse.DeleteLoungeDowngradeResult> deleteLoungeDowngradeResult;

    /**
     * Gets the value of the deleteLoungeDowngradeResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DeleteLoungeDowngradeResponse.DeleteLoungeDowngradeResult }{@code >}
     *     
     */
    public JAXBElement<DeleteLoungeDowngradeResponse.DeleteLoungeDowngradeResult> getDeleteLoungeDowngradeResult() {
        return deleteLoungeDowngradeResult;
    }

    /**
     * Sets the value of the deleteLoungeDowngradeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DeleteLoungeDowngradeResponse.DeleteLoungeDowngradeResult }{@code >}
     *     
     */
    public void setDeleteLoungeDowngradeResult(JAXBElement<DeleteLoungeDowngradeResponse.DeleteLoungeDowngradeResult> value) {
        this.deleteLoungeDowngradeResult = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;any processContents='lax' minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class DeleteLoungeDowngradeResult {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
