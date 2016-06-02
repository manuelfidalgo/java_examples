import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProductMatcher {

    private static final String CRAWLED_DATA_PATH = "/home/mfidalgo/temp/scrapy/cz/orlais.csv";
    private static final String PRODUCTS_PATH = "/home/mfidalgo/temp/Java/productos";
    private static final char SEPARATOR = ',';

    private List<CrawledData> readCrawled() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(CRAWLED_DATA_PATH),
                Charset.defaultCharset());
        List<CrawledData> products = new ArrayList<CrawledData>(lines.size());
        for (String line : lines) { // url,price,title
            // http://mifarmaciaonline.es/nutriben-zumo-de-frutas-variadas-2x130ml/107p,"3,50 €",Nutriben
            // Zumo de Frutas Variadas 2x130ml
            int firstComma = line.indexOf(SEPARATOR);
            int lastComma = line.lastIndexOf(SEPARATOR);
            String url = line.substring(0, firstComma);
            String price = line.substring(firstComma + 1, lastComma);
            String description = line.substring(lastComma);
            CrawledData data = new CrawledData(url, price, description);
            products.add(data);
        }
        return products;
    }

    private List<String> readProducts() throws IOException {
        return Files.readAllLines(Paths.get(PRODUCTS_PATH),
                Charset.defaultCharset());
    }

    private void productForURL(List<String> productNames,
            List<CrawledData> crawledData) {
        String product = null;
        for (String productName : productNames) {
            if (productName.startsWith("#"))
                continue;
            double currentSimilarity = 0d;
            String goodDescription = null;
            for (CrawledData crawled : crawledData) {
                String description = crawled.description.toUpperCase();
                productName = productName.toUpperCase();
                double similarity = StringSimilarity.similarity(productName,
                        description);

                if (similarity > currentSimilarity) {
                    currentSimilarity = similarity;
                    product = productName;
                    goodDescription = crawled.description;
                }
            }
            System.out.println(product + "\t" + goodDescription + "\t"
                    + currentSimilarity);

        }

    }

    public static void main(String[] args) throws IOException {
        ProductMatcher pm = new ProductMatcher();
        List<CrawledData> crawledProducts = pm.readCrawled();
        List<String> products = pm.readProducts();
        pm.productForURL(products, crawledProducts);
    }

    private static class CrawledData {

        public CrawledData(String url, String price, String description) {
            this.url = url;
            this.price = price;
            this.description = description;
        }

        String url;
        String price;
        String description;

        @Override
        public String toString() {
            return "CrawledData [url=" + url + ", price=" + price
                    + ", description=" + description + "]";
        }

    }

}
