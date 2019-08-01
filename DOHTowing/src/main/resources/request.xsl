<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:template match="/">
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Header correlationID="%s"></soap:Header>
  <soap:Body>
	<xsl:copy-of select="/" />
  </soap:Body>
 </soap:Envelope>	
	</xsl:template>
</xsl:stylesheet>