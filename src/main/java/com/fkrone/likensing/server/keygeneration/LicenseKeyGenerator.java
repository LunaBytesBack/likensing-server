package com.fkrone.likensing.server.keygeneration;

import com.google.common.io.BaseEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sirius.kernel.Sirius;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.Exceptions;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Generates a new license key.
 */
@Register(classes = LicenseKeyGenerator.class)
public class LicenseKeyGenerator {

    /**
     * Generates a new license key for the provided properties.
     *
     * @param featuresList the features to grant
     * @param validUntil   how long the license should be valid
     * @param scopeUid     the uid of the scope to generate the license for
     * @return the base64 encoded license
     */
    public String generateLicense(List<String> featuresList, LocalDate validUntil, String scopeUid) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = readPrivateKey(keyFactory);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            Document document = initializeDocument();

            Element licensedProperties = generateLicensedProperties(document, featuresList, validUntil, scopeUid);

            byte[] licenseBody = nodeToBytes(licensedProperties);

            signature.update(licenseBody);

            byte[] signedLicense = signature.sign();

            String signedLicenseString = BaseEncoding.base64().encode(signedLicense);

            return BaseEncoding.base64()
                               .encode(writeFinalLicenseXML(document, licensedProperties, signedLicenseString));
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private PrivateKey readPrivateKey(KeyFactory keyFactory) throws InvalidKeySpecException {
        String privateKeyString = Sirius.getSettings().getString("licensing.privateKey");

        if (Strings.isEmpty(privateKeyString)) {
            throw Exceptions.createHandled().withNLSKey("LicenseKeyGenerator.errPrivateKeyNotConfigured").handle();
        }

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(privateKeyString)));
    }

    private Document initializeDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

    /**
     * Generates the element containing the licensed properties
     * which will be signed and also be included in the final license.
     *
     * @param document     the document to build the properties on
     * @param featuresList the list of features to include in the license
     * @param validUntil   how long the license is valid
     * @param scopeUid     the uid of the scope the license is for
     * @return an {@link Element} containing the licensed properties
     */
    private Element generateLicensedProperties(Document document,
                                               List<String> featuresList,
                                               LocalDate validUntil,
                                               String scopeUid) {
        Element licensedPropertiesElement = document.createElement("licensedProperties");
        Element licensedFeaturesElement = document.createElement("licensedFeatures");

        featuresList.stream()
                    .map(feature -> generateFeatureElement(document, feature))
                    .forEach(licensedFeaturesElement::appendChild);
        licensedPropertiesElement.appendChild(licensedFeaturesElement);

        Element validUntilElement = document.createElement("validUntil");
        validUntilElement.appendChild(document.createTextNode(convertLocalDateToText(validUntil)));
        licensedPropertiesElement.appendChild(validUntilElement);

        Element scopeUidElement = document.createElement("scopeUid");
        scopeUidElement.appendChild(document.createTextNode(scopeUid));
        licensedPropertiesElement.appendChild(scopeUidElement);

        return licensedPropertiesElement;
    }

    private String convertLocalDateToText(LocalDate localDate) {
        return String.valueOf(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    private Element generateFeatureElement(Document document, String featureName) {
        Element featureNode = document.createElement("feature");
        featureNode.appendChild(document.createTextNode(featureName));
        return featureNode;
    }

    /**
     * Transforms a node to its byte representation.
     *
     * @param node the node to transform
     * @return a byte array representing the supplied node
     * @throws TransformerException if the transformation process fails
     * @throws IOException          if the internal handling of the node fails
     */
    private byte[] nodeToBytes(Node node) throws TransformerException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream))) {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        }
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] writeFinalLicenseXML(Document document,
                                        Element licensedPropertiesElement,
                                        String signedLicenseString) throws TransformerException, IOException {

        Element licenseWrapper = document.createElement("license");
        document.appendChild(licenseWrapper);
        licenseWrapper.appendChild(licensedPropertiesElement);

        Element signKeyElement = document.createElement("signKey");
        signKeyElement.appendChild(document.createTextNode(signedLicenseString));
        licenseWrapper.appendChild(signKeyElement);

        return nodeToBytes(document);
    }
}
