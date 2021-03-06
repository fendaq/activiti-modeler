import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.catalina.core.ApplicationContext;

import com.intalio.web.plugin.IDiagramPlugin;
import com.intalio.web.plugin.impl.LocalPluginImpl;

public class PluginReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String basePath = "D:/AppServer/apache-tomcat-7.0.14/wtpwebapps/activiti-modeler";
		FileInputStream fileStream = null;
		ServletContext context = null;
		Map<String, IDiagramPlugin> local = new HashMap<String, IDiagramPlugin>();
		try {
			try {
				fileStream = new FileInputStream(new StringBuilder(context.getRealPath("/")).append("/").
                        append("js").append("/").append("Plugins").append("/").
                        append("plugins.xml").toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(fileStream);
			while (reader.hasNext()) {
				if (reader.next() == XMLStreamReader.START_ELEMENT) {
					if ("plugin".equals(reader.getLocalName())) {
						String source = null, name = null;
						boolean core = false;
						for (int i = 0; i < reader.getAttributeCount(); i++) {
							if ("source"
									.equals(reader.getAttributeLocalName(i))) {
								source = reader.getAttributeValue(i);
							} else if ("name".equals(reader
									.getAttributeLocalName(i))) {
								name = reader.getAttributeValue(i);
							} else if ("core".equals(reader
									.getAttributeLocalName(i))) {
								core = Boolean.parseBoolean(reader
										.getAttributeValue(i));
							}
						}
						Map<String, Object> props = new HashMap<String, Object>();
						while (reader.hasNext()) {
							int ev = reader.next();
							if (ev == XMLStreamReader.START_ELEMENT) {
								if ("property".equals(reader.getLocalName())) {
									String key = null, value = null;
									for (int i = 0; i < reader
											.getAttributeCount(); i++) {
										if ("name".equals(reader
												.getAttributeLocalName(i))) {
											key = reader.getAttributeValue(i);
										} else if ("value".equals(reader
												.getAttributeLocalName(i))) {
											value = reader.getAttributeValue(i);
										}
									}
									if (key != null & value != null)
										props.put(key, value);
								}
							} else if (ev == XMLStreamReader.END_ELEMENT) {
								if ("plugin".equals(reader.getLocalName())) {
									break;
								}
							}
						}
						local.put(name, new LocalPluginImpl(name, source, context, core, props));
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e); // stop initialization
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
				}
			}
			;
		}
	}

}
