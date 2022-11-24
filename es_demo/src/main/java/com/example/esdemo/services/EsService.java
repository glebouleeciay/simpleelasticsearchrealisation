package com.example.esdemo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsService {

    public static class Market_products {

        private String name;
        private String description;
        private int price;
        private int in_stock;
        private int article_in_stock;
        private String delivered_to_the_store;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getIn_stock() {
            return in_stock;
        }

        public void setIn_stock(int in_stock) {
            this.in_stock = in_stock;
        }

        public int getArticle_in_stock() {
            return article_in_stock;
        }

        public void setArticle_in_stock(int article_in_stock) {
            this.article_in_stock = article_in_stock;
        }

        public String getDelivered_to_the_store() {
            return delivered_to_the_store;
        }

        public void setDelivered_to_the_store(String delivered_to_the_store) {
            this.delivered_to_the_store = delivered_to_the_store;
        }

    }

    private final static String INDEX_NAME = "market_products";

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestHighLevelClient esClient;

    public EsService(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    public void updateMarket_products(String id, String name, String description, int price, int in_stock, int article_in_stock, String delivered_to_the_store) throws Exception {
        Market_products products = new Market_products();
        products.setName(name);
        products.setDescription(description);
        products.setPrice(price);
        products.setIn_stock(in_stock);
        products.setArticle_in_stock(article_in_stock);
        products.setDelivered_to_the_store(delivered_to_the_store);
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(id);
        indexRequest.source(mapper.writeValueAsString(products), XContentType.JSON);

        esClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<Market_products> searchdescription(String searchString) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder fuzzyQuery = QueryBuilders
                .matchQuery("description", searchString)
                .fuzziness(Fuzziness.AUTO);
        QueryBuilder wildcardQuery = QueryBuilders
                .wildcardQuery("description", "*" + searchString + "*");
        QueryBuilder searchQuery = QueryBuilders
                .boolQuery()
                .should(fuzzyQuery)
                .should(wildcardQuery);
        searchSourceBuilder.query(searchQuery);


        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Market_products> market_products = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String description = (String) sourceAsMap.get("description");
            int price = (Integer) sourceAsMap.get("price");
            int in_stock = (Integer) sourceAsMap.get("in_stock");
            int article_in_stock = (Integer) sourceAsMap.get("article_in_stock");
            String delivered_to_the_store = (String) sourceAsMap.get("delivered_to_the_store");

            HighlightField highlightFieldText = hit.getHighlightFields().get("description");
            if (highlightFieldText != null && highlightFieldText.fragments().length > 0) {
                description = highlightFieldText.fragments()[0].toString();
            }

            Market_products products = new Market_products();
            products.setName(name);
            products.setDescription(description);
            products.setPrice(price);
            products.setIn_stock(in_stock);
            products.setArticle_in_stock(article_in_stock);
            products.setDelivered_to_the_store(delivered_to_the_store);
            market_products.add(products);
        }

        return market_products;
    }
}
