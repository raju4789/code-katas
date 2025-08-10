package com.raju.codekatas.refactoring.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LEGACY CODE - Complex product filtering with nested conditionals
 * 
 * REFACTORING CHALLENGE:
 * Convert this complex filtering logic into composable Specification Pattern.
 * 
 * TIME LIMIT: 40 minutes
 * 
 * REQUIREMENTS:
 * 1. Create Specification interface with isSatisfiedBy() method
 * 2. Implement concrete specifications for each filter criteria
 * 3. Support AND, OR, NOT operations for combining specifications
 * 4. Make specifications reusable and testable
 * 5. Replace all the nested if/else logic with specification composition
 */
public class ProductFilterL {
    
    public List<Product> filterProducts(List<Product> products, FilterCriteria criteria) {
        System.out.println("Filtering " + products.size() + " products with complex criteria");
        
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : products) {
            boolean matches = true;
            
            // Price range filtering
            if (criteria.getMinPrice() != null) {
                if (product.getPrice().compareTo(criteria.getMinPrice()) < 0) {
                    matches = false;
                    continue;
                }
            }
            
            if (criteria.getMaxPrice() != null) {
                if (product.getPrice().compareTo(criteria.getMaxPrice()) > 0) {
                    matches = false;
                    continue;
                }
            }
            
            // Category filtering
            if (criteria.getCategories() != null && !criteria.getCategories().isEmpty()) {
                if (!criteria.getCategories().contains(product.getCategory())) {
                    matches = false;
                    continue;
                }
            }
            
            // Brand filtering
            if (criteria.getBrands() != null && !criteria.getBrands().isEmpty()) {
                if (!criteria.getBrands().contains(product.getBrand())) {
                    matches = false;
                    continue;
                }
            }
            
            // In stock filtering
            if (criteria.isInStockOnly()) {
                if (product.getStockQuantity() <= 0) {
                    matches = false;
                    continue;
                }
            }
            
            // Rating filtering
            if (criteria.getMinRating() != null) {
                if (product.getRating() < criteria.getMinRating()) {
                    matches = false;
                    continue;
                }
            }
            
            // Discount filtering
            if (criteria.isOnSaleOnly()) {
                if (product.getDiscountPercentage() <= 0) {
                    matches = false;
                    continue;
                }
            }
            
            // Date filtering (new products)
            if (criteria.getNewProductsSince() != null) {
                if (product.getCreatedDate().isBefore(criteria.getNewProductsSince())) {
                    matches = false;
                    continue;
                }
            }
            
            // Keyword search in name and description
            if (criteria.getSearchKeyword() != null && !criteria.getSearchKeyword().isEmpty()) {
                String keyword = criteria.getSearchKeyword().toLowerCase();
                boolean keywordFound = product.getName().toLowerCase().contains(keyword) ||
                                    product.getDescription().toLowerCase().contains(keyword);
                if (!keywordFound) {
                    matches = false;
                    continue;
                }
            }
            
            // Tag filtering
            if (criteria.getTags() != null && !criteria.getTags().isEmpty()) {
                boolean hasAnyTag = false;
                for (String tag : criteria.getTags()) {
                    if (product.getTags().contains(tag)) {
                        hasAnyTag = true;
                        break;
                    }
                }
                if (!hasAnyTag) {
                    matches = false;
                    continue;
                }
            }
            
            // Premium products filtering
            if (criteria.isPremiumOnly()) {
                if (product.getPrice().compareTo(new BigDecimal("100")) < 0 || product.getRating() < 4.0) {
                    matches = false;
                    continue;
                }
            }
            
            // Featured products filtering
            if (criteria.isFeaturedOnly()) {
                if (!product.isFeatured()) {
                    matches = false;
                    continue;
                }
            }
            
            if (matches) {
                filteredProducts.add(product);
            }
        }
        
        System.out.println("Filtered to " + filteredProducts.size() + " products");
        return filteredProducts;
    }
    
    public List<Product> filterProductsAdvanced(List<Product> products, AdvancedFilterCriteria criteria) {
        System.out.println("Advanced filtering with complex business rules");
        
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : products) {
            boolean matches = true;
            
            // Complex business rule: Electronics on sale
            if (criteria.isElectronicsOnSale()) {
                if (!"ELECTRONICS".equals(product.getCategory()) || product.getDiscountPercentage() <= 0) {
                    matches = false;
                    continue;
                }
            }
            
            // Complex business rule: High-value items
            if (criteria.isHighValueItems()) {
                if (product.getPrice().compareTo(new BigDecimal("500")) < 0 || 
                    product.getRating() < 4.5 || 
                    product.getStockQuantity() < 10) {
                    matches = false;
                    continue;
                }
            }
            
            // Complex business rule: Popular new arrivals
            if (criteria.isPopularNewArrivals()) {
                LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
                if (product.getCreatedDate().isBefore(thirtyDaysAgo) || 
                    product.getRating() < 4.0 || 
                    product.getStockQuantity() < 5) {
                    matches = false;
                    continue;
                }
            }
            
            // Complex business rule: Clearance items
            if (criteria.isClearanceItems()) {
                if (product.getDiscountPercentage() < 30 || 
                    product.getStockQuantity() > 5 || 
                    product.isFeatured()) {
                    matches = false;
                    continue;
                }
            }
            
            // Complex business rule: Luxury items
            if (criteria.isLuxuryItems()) {
                if (product.getPrice().compareTo(new BigDecimal("1000")) < 0 || 
                    !Arrays.asList("JEWELRY", "WATCHES", "FASHION").contains(product.getCategory()) ||
                    product.getRating() < 4.5) {
                    matches = false;
                    continue;
                }
            }
            
            if (matches) {
                filteredProducts.add(product);
            }
        }
        
        return filteredProducts;
    }
    
    // Data classes
    public static class Product {
        private final String id;
        private final String name;
        private final String description;
        private final BigDecimal price;
        private final String category;
        private final String brand;
        private final int stockQuantity;
        private final double rating;
        private final double discountPercentage;
        private final LocalDate createdDate;
        private final List<String> tags;
        private final boolean featured;
        
        public Product(String id, String name, String description, BigDecimal price, String category,
                      String brand, int stockQuantity, double rating, double discountPercentage,
                      LocalDate createdDate, List<String> tags, boolean featured) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.brand = brand;
            this.stockQuantity = stockQuantity;
            this.rating = rating;
            this.discountPercentage = discountPercentage;
            this.createdDate = createdDate;
            this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
            this.featured = featured;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public String getCategory() { return category; }
        public String getBrand() { return brand; }
        public int getStockQuantity() { return stockQuantity; }
        public double getRating() { return rating; }
        public double getDiscountPercentage() { return discountPercentage; }
        public LocalDate getCreatedDate() { return createdDate; }
        public List<String> getTags() { return new ArrayList<>(tags); }
        public boolean isFeatured() { return featured; }
    }
    
    public static class FilterCriteria {
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private List<String> categories;
        private List<String> brands;
        private boolean inStockOnly;
        private Double minRating;
        private boolean onSaleOnly;
        private LocalDate newProductsSince;
        private String searchKeyword;
        private List<String> tags;
        private boolean premiumOnly;
        private boolean featuredOnly;
        
        // Getters and setters
        public BigDecimal getMinPrice() { return minPrice; }
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
        public BigDecimal getMaxPrice() { return maxPrice; }
        public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
        public List<String> getBrands() { return brands; }
        public void setBrands(List<String> brands) { this.brands = brands; }
        public boolean isInStockOnly() { return inStockOnly; }
        public void setInStockOnly(boolean inStockOnly) { this.inStockOnly = inStockOnly; }
        public Double getMinRating() { return minRating; }
        public void setMinRating(Double minRating) { this.minRating = minRating; }
        public boolean isOnSaleOnly() { return onSaleOnly; }
        public void setOnSaleOnly(boolean onSaleOnly) { this.onSaleOnly = onSaleOnly; }
        public LocalDate getNewProductsSince() { return newProductsSince; }
        public void setNewProductsSince(LocalDate newProductsSince) { this.newProductsSince = newProductsSince; }
        public String getSearchKeyword() { return searchKeyword; }
        public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public boolean isPremiumOnly() { return premiumOnly; }
        public void setPremiumOnly(boolean premiumOnly) { this.premiumOnly = premiumOnly; }
        public boolean isFeaturedOnly() { return featuredOnly; }
        public void setFeaturedOnly(boolean featuredOnly) { this.featuredOnly = featuredOnly; }
    }
    
    public static class AdvancedFilterCriteria {
        private boolean electronicsOnSale;
        private boolean highValueItems;
        private boolean popularNewArrivals;
        private boolean clearanceItems;
        private boolean luxuryItems;
        
        // Getters and setters
        public boolean isElectronicsOnSale() { return electronicsOnSale; }
        public void setElectronicsOnSale(boolean electronicsOnSale) { this.electronicsOnSale = electronicsOnSale; }
        public boolean isHighValueItems() { return highValueItems; }
        public void setHighValueItems(boolean highValueItems) { this.highValueItems = highValueItems; }
        public boolean isPopularNewArrivals() { return popularNewArrivals; }
        public void setPopularNewArrivals(boolean popularNewArrivals) { this.popularNewArrivals = popularNewArrivals; }
        public boolean isClearanceItems() { return clearanceItems; }
        public void setClearanceItems(boolean clearanceItems) { this.clearanceItems = clearanceItems; }
        public boolean isLuxuryItems() { return luxuryItems; }
        public void setLuxuryItems(boolean luxuryItems) { this.luxuryItems = luxuryItems; }
    }
    
    public static void main(String[] args) {
        ProductFilterL filter = new ProductFilterL();
        
        // Create test products
        List<Product> products = Arrays.asList(
            new Product("P1", "iPhone 14", "Latest smartphone", new BigDecimal("999"), "ELECTRONICS", 
                       "Apple", 10, 4.5, 10, LocalDate.now().minusDays(15), 
                       Arrays.asList("smartphone", "mobile"), true),
            new Product("P2", "Samsung TV", "4K Smart TV", new BigDecimal("1200"), "ELECTRONICS", 
                       "Samsung", 5, 4.2, 0, LocalDate.now().minusDays(45), 
                       Arrays.asList("tv", "smart"), false),
            new Product("P3", "Nike Shoes", "Running shoes", new BigDecimal("150"), "FASHION", 
                       "Nike", 20, 4.0, 25, LocalDate.now().minusDays(10), 
                       Arrays.asList("shoes", "running"), false)
        );
        
        // Test basic filtering
        FilterCriteria criteria = new FilterCriteria();
        criteria.setMinPrice(new BigDecimal("100"));
        criteria.setInStockOnly(true);
        criteria.setMinRating(4.0);
        
        List<Product> filtered = filter.filterProducts(products, criteria);
        System.out.println("Basic filtered products: " + filtered.size());
        
        // Test advanced filtering
        AdvancedFilterCriteria advancedCriteria = new AdvancedFilterCriteria();
        advancedCriteria.setElectronicsOnSale(true);
        
        List<Product> advancedFiltered = filter.filterProductsAdvanced(products, advancedCriteria);
        System.out.println("Advanced filtered products: " + advancedFiltered.size());
    }
}
