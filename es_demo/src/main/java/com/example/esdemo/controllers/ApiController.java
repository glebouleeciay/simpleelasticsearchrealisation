package com.example.esdemo.controllers;

import com.example.esdemo.services.EsService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EsService esService;

    public ApiController(EsService esService) {
        this.esService = esService;
    }

    @PutMapping("/market_products")
    public String addMarket_products(@RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("price") int price,
                                     @RequestParam("in_stock") int in_stock, @RequestParam("article") int article_in_the_stock,
                                     @RequestParam("delivery_date") String delivered_to_the_store) throws Exception {
        String id = UUID.randomUUID().toString();
        esService.updateMarket_products(id, name, description, price, in_stock, article_in_the_stock, delivered_to_the_store);
        return id;
    }

    @GetMapping("/searchdescription")
    public List<EsService.Market_products> tagsSearch(@RequestParam("query") String query) throws Exception {
        return esService.searchdescription(query);
    }

}
