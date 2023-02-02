import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String fileName = "data.xml";
        String resultFileName = "data2.json";

        List<Employee> list = parseXML(fileName);
        System.out.println("Список сотрудников получен. Количество сотрудников: " + list.size());
        String json = listToJson(list);
        if (writeString(json, resultFileName)) {
            System.out.println("Файл " + resultFileName + " создан.");
        } else {
            System.out.println("Запись результата не была произведена.");
        }
    }

    static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> result = new ArrayList<Employee>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            long id = 0L;
            String firstName = null, lastName = null, country = null;
            int age = 0;
            if (Node.ELEMENT_NODE == nodeList.item(i).getNodeType()) {
                NodeList employeeFields = nodeList.item(i).getChildNodes();
                for (int j = 0; j < employeeFields.getLength(); j++) {
                    if (Node.ELEMENT_NODE == employeeFields.item(j).getNodeType()) {
                        String attrName = employeeFields.item(j).getNodeName();
                        String attrValue = employeeFields.item(j).getTextContent();
                        if (attrName.equals("id")) {
                            id = Long.parseLong(attrValue);
                        } else if (attrName.equals("firstName")) {
                            firstName = attrValue;
                        } else if (attrName.equals("lastName")) {
                            lastName = attrValue;
                        } else if (attrName.equals("country")) {
                            country = attrValue;
                        } else if (attrName.equals("age")) {
                            age = Integer.parseInt(attrValue);
                        }
                    }
                }
                result.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return result;
    }

    static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    static boolean writeString(String json, String resultFileName) {
        try(FileWriter writer = new FileWriter(resultFileName, false))
        {
            writer.write(json);
            writer.flush();
            return true;
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
