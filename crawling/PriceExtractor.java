import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class PriceExtractor {

    private static final String XPATH_EXPRESSION = "id('precio_html')";
    private static final String URLS_FILE_PATH = "/home/mfidalgo/personal/crawl/mifarmaciaonline_urls.csv";
    private static final String URLS_FILE_PATH_TEST = "/home/mfidalgo/personal/crawl/mifarmaciaonline_urls_test.csv";
    private static final String DOMAIN = "http://mifarmaciaonline.es/";
    private static final String PRODUCTS_PATH = "/home/mfidalgo/personal/crawl/productos";
    private static final String REPORT_PATH = "/home/mfidalgo/personal/crawl/out.csv";

    private static final String CORRECT_URLS_PATH = "/home/mfidalgo/personal/crawl/correct_urls";

    private static final char REPORT_SEPARATOR = '|';

    private XPath xpath;
    private XMLReader htmlParser;
    private Transformer transformer;

    public PriceExtractor() throws SAXNotRecognizedException,
            SAXNotSupportedException, TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        XPathFactory xpathFac = XPathFactory.newInstance();
        xpath = xpathFac.newXPath();

        htmlParser = new Parser();
        htmlParser.setFeature(Parser.namespacesFeature, true);
        transformer = TransformerFactory.newInstance().newTransformer();
    }

    private List<String> readProducts() throws IOException {
        return Files.readAllLines(Paths.get(PRODUCTS_PATH),
                Charset.defaultCharset());
    }

    private List<String> readURLS() throws IOException {

        Path urlsPath = Paths.get(URLS_FILE_PATH);
        List<String> lines = Files.readAllLines(urlsPath,
                Charset.defaultCharset());

        String[] tokens;
        List<String> urls = new ArrayList<>();
        for (String line : lines) {

            tokens = line.split(",");
            for (String token : tokens) {
                token = token.replace("\"", "");
                if (isGoodURL(token)) {
                    if (!urls.contains(token)) {
                        urls.add(token);
                    }
                }
            }
        }
        return urls;
    }

    private boolean isGoodURL(String text) {
        return text.startsWith("http") && !text.endsWith(".jpg");
    }

    private Node getNode(String url) {
        Node htmlNode = null;
        DOMResult result = new DOMResult();
        try (InputStream input = new URL(url).openConnection().getInputStream();) {
            transformer.transform(new SAXSource(htmlParser, new InputSource(
                    input)), result);
            htmlNode = result.getNode();
        } catch (IOException e) {
            System.err.println("Exception reading url " + url);
        } catch (TransformerException e) {
            System.err.println("Exception with html in url " + url);
        }
        return htmlNode;

    }

    private String obtainPrice(String url) throws XPathExpressionException {
        String price = "";
        Node htmlNode = getNode(url);
        if (htmlNode != null) {
            NodeList nodes = (NodeList) xpath.evaluate(XPATH_EXPRESSION,
                    htmlNode, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                Node firstChild = node.getFirstChild();
                price = firstChild.getNodeValue();
            }
        }
        return price;
    }

    private void saveProductsInfo(List<String> products) throws IOException {
        Files.write(Paths.get(REPORT_PATH), products, Charset.defaultCharset());
    }

    private void saveCorrectUrls(List<String> products) throws IOException {
        Files.write(Paths.get(CORRECT_URLS_PATH), products,
                Charset.defaultCharset());
    }

    private String getName(String removePrefix, String url) {
        String name = url.replaceFirst(removePrefix, "");
        int endName = name.indexOf("/");
        if (endName != -1)
            name = name.substring(0, endName);

        return name;

    }

    public static void main(String[] args) throws Exception {

        PriceExtractor prueba = new PriceExtractor();

        String price = null;
        List<String> products = new ArrayList<String>();
        String product;
        List<String> correctUrls = new ArrayList<String>();
        for (String url : prueba.readURLS()) {

            price = prueba.obtainPrice(url);
            if (!price.isEmpty()) {

                product = prueba.getName(DOMAIN, url) + REPORT_SEPARATOR + url
                        + REPORT_SEPARATOR + price;

                products.add(product);
                correctUrls.add(url);
            } else {
                // TODO list urls with no price??
            }
        }

        // List<String> productNames = prueba.readProducts();
        // prueba.productForURL(urls, productNames);
        prueba.saveCorrectUrls(correctUrls);
        prueba.saveProductsInfo(products);
    }

    // static class ProductInfo {
    //
    // String name;
    // String url;
    // String price;
    //
    // public ProductInfo(String name, String url, String price) {
    // super();
    // this.name = name;
    // this.url = url;
    // this.price = price;
    // }
    //
    // @Override
    // public String toString() {
    // return "ProductInfo [name=" + name + ", url=" + url + ", price="
    // + price + "]";
    // }
    //
    // }

}
