package net.unibld.core.util.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBDateTimeAdapter extends XmlAdapter<String, Date> {
	 // the desired format
	 private String pattern = "yyyy.MM.dd. HH:ss";
	
	 public String marshal(Date date) throws Exception {
		 if (date==null) {
			 return null;
		 }
		 return new SimpleDateFormat(pattern).format(date);
	 }

	 public Date unmarshal(String dateString) throws Exception {
		 if (dateString==null) {
			 return null;
		 }
		 return new SimpleDateFormat(pattern).parse(dateString);
	 }

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
