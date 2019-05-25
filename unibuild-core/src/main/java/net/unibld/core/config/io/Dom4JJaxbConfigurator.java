package net.unibld.core.config.io;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.unibld.core.build.BuildException;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.VariableSupport;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A configurator base class for configurators that combine {@link JaxbConfigurator}
 * and DOM4J dynamic tags and attributes
 * @author andor
 *
 * @param <T> Generic configuration type
 */
public abstract class Dom4JJaxbConfigurator<T> extends JaxbConfigurator<T>{
	private static final String ERR_WRITE_METHOD_NOT_FOUND = "Write method not found for: {}.{}";

	private static final String ERR_READ_METHOD_NOT_FOUND = "Read method not found for: {}.{}";

	private static final String ERROR_OBJECT_NULL = "Object was null";

	private static final String ERR_PROPERTY_DESCRIPTOR_NULL = "PropertyDescriptor was null";

	private static final Logger LOG=LoggerFactory.getLogger(Dom4JJaxbConfigurator.class);
	
	private List<String> domPaths=new ArrayList<>();
	
	private List<Class<?>> supportedAttributeTypes=new ArrayList<>();

	
	
	@Autowired
	private VariableSupport vars;
	
	
	/**
	 * Default constructor that sets up default dynamic attribute types 
	 * using addDefaultAttributeTypes(). 
	 * @param c Build context attribute container
	 */
	public Dom4JJaxbConfigurator() {
		addDefaultAttributeTypes();
	}
	
	protected void addDefaultAttributeTypes() {
		supportedAttributeTypes.add(String.class);
		
		supportedAttributeTypes.add(int.class);
		supportedAttributeTypes.add(Integer.class);
		supportedAttributeTypes.add(long.class);
		supportedAttributeTypes.add(Long.class);
		supportedAttributeTypes.add(short.class);
		supportedAttributeTypes.add(Short.class);
		
		supportedAttributeTypes.add(float.class);
		supportedAttributeTypes.add(Float.class);
		supportedAttributeTypes.add(double.class);
		supportedAttributeTypes.add(Double.class);
		
		supportedAttributeTypes.add(byte.class);
		supportedAttributeTypes.add(Byte.class);
		supportedAttributeTypes.add(boolean.class);
		supportedAttributeTypes.add(Boolean.class);
		supportedAttributeTypes.add(char.class);
		
		
		
	}

	/**
	 * Adds a new dynamic DOM4J path to the configurator. The path will be
	 * scanned for dynamic tags.
	 * @param path DOM4J dynamic path to scan
	 */
	public void addDomPath(String path) {
		domPaths.add(path);
	}
	
	/**
	 * Clears DOM4J paths
	 */
	public void clearDomPaths() {
		domPaths.clear();
	}

	@Override
	public T readConfig(IBuildContextAttributeContainer context,Class<T> klazz, InputStream is) throws Exception {
		String xml=IOUtils.toString(is,StandardCharsets.UTF_8.name());
		
		ByteArrayInputStream bis=new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()));
		
		T ret = super.readConfig(context,klazz, bis);
		jaxbInstanceLoaded(ret);
		Document document = DocumentHelper.parseText(xml);
        readDomContent(context,document, ret);
		return ret;
	}

	protected void jaxbInstanceLoaded(T ret) {
		if (ret!=null) {
			LOG.debug("JAXB object loaded: {}",ret.getClass().getName());
		}
	}

	@Override
	public void writeConfig(OutputStream out, T config) throws Exception {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		super.writeConfig(bos, config);
		
		byte[] bytes = bos.toByteArray();
		String str=new String(bytes,"UTF-8");
		String fullXml=addDomContent(str,config);
		
		out.write(fullXml.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	protected void readDomContent(IBuildContextAttributeContainer context,Document doc,T config) {
		int idx=0;
		for (String path:domPaths) {
			LOG.info("Reading DOM path: {}...",path);
			readDomPath(context,doc,path,idx,config);
			idx++;
		}
		
		LOG.debug("Finished reading DOM paths");
	}
	
	protected String addDomContent(String inXml,T config) throws DocumentException, IOException {
		Document document = DocumentHelper.parseText(inXml);
		int idx=0;
		for (String path:domPaths) {
			LOG.info("Adding DOM path: {}...",path);
			addDomPath(document,path,idx,config);
			idx++;
		}
		
		return writeDomDocument(document);
	}
	
	 protected abstract void addDomPath(Document document, String path,int idx,T config);
	 protected abstract void readDomPath(IBuildContextAttributeContainer context,Document document, String path,int idx,T config);

	protected String writeDomDocument(Document document) throws IOException {
		 	OutputFormat format = OutputFormat.createPrettyPrint();
	        
	        // lets write to a file
	        StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(
	            sw,format
	        );
	        writer.write( document );
	        writer.close();

	        return sw.getBuffer().toString();
	    }
	
	protected String convertToStringValue(Class<?> propertyType, Object val) {
		if (propertyType==null) {
			throw new IllegalArgumentException("Property type was null");
		}
		if (val==null) {
			return null;
		}
		
		if (!isSupportedType(propertyType)) {
			LOG.info("Type not supported: {}",propertyType.getName());
			return null;
		}
		return val.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertToObjectValue(IBuildContextAttributeContainer context,Class<?> propertyType, String str,boolean substitute) {
		if (propertyType==null) {
			throw new IllegalArgumentException("Property type was null");
		}
		if (str==null||str.trim().length()==0) {
			return null;
		}
		
		if (!isSupportedType(propertyType)) {
			LOG.info("Type not supported: {}",propertyType.getName());
			return null;
		}
		
		if (substitute) {
			str=vars.substitute(str, context);
			
		} 
		
		if (propertyType.equals(String.class)) {
			return str;
		}
		if (propertyType.equals(int.class)||propertyType.equals(Integer.class)) {
			return Integer.parseInt(str.trim());
		}
		if (propertyType.equals(boolean.class)||propertyType.equals(Boolean.class)) {
			return Boolean.parseBoolean(str.trim());
		}
		
		if (isEnum(propertyType)) {
			return convertToEnumValue((Class<? extends Enum>) propertyType,str);
		}
		
		if (propertyType.equals(long.class)||propertyType.equals(Long.class)) {
			return Long.parseLong(str.trim());
		}
		if (propertyType.equals(float.class)||propertyType.equals(Float.class)) {
			return Float.parseFloat(str.trim());
		}
		if (propertyType.equals(double.class)||propertyType.equals(Double.class)) {
			return Double.parseDouble(str.trim());
		}
		if (propertyType.equals(byte.class)||propertyType.equals(Byte.class)) {
			return Byte.parseByte(str.trim());
		}
		if (propertyType.equals(short.class)||propertyType.equals(Short.class)) {
			return Short.parseShort(str.trim());
		}
		if (propertyType.equals(char.class)) {
			return str.charAt(0);
		}
		
		LOG.info("Type not found: {}",propertyType.getName());
		return null;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertToEnumValue(Class<? extends Enum> enumType, String str) {
		return Enum.valueOf(enumType, str);
		
	}

	protected boolean isSupportedType(Class<?> propertyType) {
		return supportedAttributeTypes.contains(propertyType)||isEnum(propertyType);
	}
	protected boolean isEnum(Class<?> propertyType) {
		return propertyType.isEnum();
	}
	
	protected <O> String getPropertyValue(O t,
			PropertyDescriptor propertyDescriptor) {
		if (t==null) {
			throw new IllegalArgumentException(ERROR_OBJECT_NULL);
		}
		if (propertyDescriptor==null) {
			throw new IllegalArgumentException(ERR_PROPERTY_DESCRIPTOR_NULL);
		}
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod==null) {
			LOG.debug(ERR_READ_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return null;
		}
		
		Method writeMethod = propertyDescriptor.getWriteMethod();
		if (writeMethod==null) {
			LOG.debug(ERR_WRITE_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return null;
		}
		
		try {
			Object val = readMethod.invoke(t);
			return convertToStringValue(propertyDescriptor.getPropertyType(),val);
		} catch (Exception e) {
			LOG.error("Failed to invoke read method: "+readMethod.getName(),e);
			return null;
		}
		
	}
	
	protected <O> void setPropertyValue(IBuildContextAttributeContainer context,O t,
			PropertyDescriptor propertyDescriptor,String value,boolean substitute) {
		if (t==null) {
			throw new IllegalArgumentException(ERROR_OBJECT_NULL);
		}
		if (propertyDescriptor==null) {
			throw new IllegalArgumentException(ERR_PROPERTY_DESCRIPTOR_NULL);
		}
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod==null) {
			LOG.info(ERR_READ_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return;
		}
		
		Method writeMethod = propertyDescriptor.getWriteMethod();
		if (writeMethod==null) {
			LOG.info(ERR_WRITE_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return;
		}
		
		try {
			writeMethod.invoke(t,convertToObjectValue(context,propertyDescriptor.getPropertyType(),value,substitute));
		} catch (BuildException e) {
			LOG.error("Failed to invoke write method: "+writeMethod.getName(),e);
			throw e;
		} catch (Exception e) {
			LOG.error("Failed to invoke write method: "+writeMethod.getName(),e);
			
		}
		
	}
	
	protected <O> void substitutePropertyValue(IBuildContextAttributeContainer context,O t,
			PropertyDescriptor propertyDescriptor) {
		if (t==null) {
			throw new IllegalArgumentException(ERROR_OBJECT_NULL);
		}
		if (propertyDescriptor==null) {
			throw new IllegalArgumentException(ERR_PROPERTY_DESCRIPTOR_NULL);
		}
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod==null) {
			LOG.info(ERR_READ_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return;
		}
		
		Method writeMethod = propertyDescriptor.getWriteMethod();
		if (writeMethod==null) {
			LOG.info(ERR_WRITE_METHOD_NOT_FOUND,t.getClass().getSimpleName(),propertyDescriptor.getName());
			return;
		}
		
		try {
			String value = convertToStringValue(propertyDescriptor.getPropertyType(),readMethod.invoke(t));
			writeMethod.invoke(t,convertToObjectValue(context,propertyDescriptor.getPropertyType(),value,true));
		} catch (BuildException e) {
			LOG.error("Failed to substitute: "+writeMethod.getName(),e);
			throw e;
		} catch (Exception e) {
			LOG.error("Failed to substitute: "+writeMethod.getName(),e);
			
		}
		
	}
}
