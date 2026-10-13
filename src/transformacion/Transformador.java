package transformacion;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Transformador {

    public static void main(String[] args) {

        try (FileReader lector = new FileReader(
                "persona.json", StandardCharsets.UTF_8)) {

            // 1. Leer el JSON utilizando GSON.
            Gson gson = new Gson();
            JsonArray personas = gson.fromJson(lector, JsonArray.class);

            if (personas == null || personas.size() != 1) {
                throw new IllegalArgumentException(
                        "El fichero debe contener una sola persona.");
            }

            // 2. Obtener la persona de la primera posición.
            JsonObject persona = personas.get(0).getAsJsonObject();

            // 3. Crear el documento XML y su elemento raíz.
            Document documento = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();

            Element raiz = documento.createElement("persona");
            documento.appendChild(raiz);

            // 4. Copiar los tres campos del JSON al XML.
            String[] campos = {"nombre", "apellido1", "apellido2"};

            for (String campo : campos) {

                if (!persona.has(campo) || persona.get(campo).isJsonNull()) {
                    throw new IllegalArgumentException(
                            "Falta el dato: " + campo);
                }

                Element elemento = documento.createElement(campo);
                elemento.setTextContent(persona.get(campo).getAsString());
                raiz.appendChild(elemento);
            }

            // 5. Guardar el documento en persona.xml.
            Transformer escritor = TransformerFactory.newInstance()
                    .newTransformer();

            escritor.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            escritor.setOutputProperty(OutputKeys.INDENT, "yes");

            escritor.transform(
                    new DOMSource(documento),
                    new StreamResult(new File("persona.xml")));

            System.out.println("Conversión JSON a XML completada.");
            System.out.println("Archivo generado: persona.xml");

        } catch (IOException e) {
            System.err.println("Error al leer el fichero: " + e.getMessage());

        } catch (JsonParseException | IllegalArgumentException
                | IllegalStateException e) {
            System.err.println("Error en los datos JSON: " + e.getMessage());

        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Error al crear o guardar el XML: "
                    + e.getMessage());
        }
    }
}