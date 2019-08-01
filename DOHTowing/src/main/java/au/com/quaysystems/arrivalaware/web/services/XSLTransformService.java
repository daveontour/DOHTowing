package au.com.quaysystems.arrivalaware.web.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;

public class XSLTransformService {

	private Xslt30Transformer transformer;
	private Processor processor;
	private static XSLTransformService instance;

	public XSLTransformService(String file)  {
		
		try {
			ClassPathResource cpr = new ClassPathResource(file);
			processor = new Processor(false);
			XsltCompiler compiler = processor.newXsltCompiler();
			XsltExecutable stylesheet = compiler.compile(new StreamSource(cpr.getInputStream()));
			transformer = stylesheet.load30();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private XSLTransformService(File file)  {
		
		try {
			processor = new Processor(false);
			XsltCompiler compiler = processor.newXsltCompiler();
			XsltExecutable stylesheet = compiler.compile(new StreamSource(file));
			transformer = stylesheet.load30();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String transform(String xml) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Serializer out = processor.newSerializer(baos);
		out.setOutputProperty(Serializer.Property.METHOD, "html");
		out.setOutputProperty(Serializer.Property.INDENT, "yes");
		try {
			transformer.applyTemplates(new StreamSource(new StringReader(xml)), out);
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}


	public static XSLTransformService getInstance() {
		if (instance == null) {
			instance = new XSLTransformService("flights.xsl");
		}
		return instance;
	}
	
	public static void main(String[] args) {
		XSLTransformService x = new XSLTransformService(new File ("C:/Users/dave_/Desktop/GateAssignmentDeleted.xsl"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Serializer out = x.processor.newSerializer(baos);
		out.setOutputProperty(Serializer.Property.METHOD, "html");
		out.setOutputProperty(Serializer.Property.INDENT, "yes");
		try {
			x.transformer.applyTemplates(new StreamSource(new File ("C:/Users/dave_/Desktop/GATE-ASSIGNMENT-DELETION-1.xml")), out);
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}
		
		System.out.println(baos.toString());
	}
}