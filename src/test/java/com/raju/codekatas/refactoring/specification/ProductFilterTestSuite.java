package com.raju.codekatas.refactoring.specification;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Specification Pattern refactoring challenge.
 * Tests expected behavior for the refactored composable filtering system.
 */
@DisplayName("Product Filter Specification Test Suite")
public class ProductFilterTestSuite {
    
    private ProductFilterL productFilter;
    private List<ProductFilterL.Product> sampleProducts;
    
    @BeforeEach
    void setUp() {
        productFilter = new ProductFilterL();
        sampleProducts = createSampleProducts();
    }
    
    private List<ProductFilterL.Product> createSampleProducts() {
        return Arrays.asList(
            new ProductFilterL.Product("P1", "iPhone 14", "Latest smartphone from Apple", 
                new BigDecimal("999"), "ELECTRONICS", "Apple", 10, 4.5, 10, 
                LocalDate.now().minusDays(15), Arrays.asList("smartphone", "mobile"), true),
            
            new ProductFilterL.Product("P2", "Samsung TV", "4K Smart TV with HDR", 
                new BigDecimal("1200"), "ELECTRONICS", "Samsung", 5, 4.2, 0, 
                LocalDate.now().minusDays(45), Arrays.asList("tv", "smart"), false),
            
            new ProductFilterL.Product("P3", "Nike Shoes", "Professional running shoes", 
                new BigDecimal("150"), "FASHION", "Nike", 20, 4.0, 25, 
                LocalDate.now().minusDays(10), Arrays.asList("shoes", "running"), false),
            
            new ProductFilterL.Product("P4", "Rolex Watch", "Luxury Swiss watch", 
                new BigDecimal("5000"), "JEWELRY", "Rolex", 2, 4.8, 0, 
                LocalDate.now().minusDays(30), Arrays.asList("luxury", "watch"), true),
            
            new ProductFilterL.Product("P5", "Dell Laptop", "Business laptop with SSD", 
                new BigDecimal("800"), "ELECTRONICS", "Dell", 0, 4.1, 15, 
                LocalDate.now().minusDays(5), Arrays.asList("laptop", "business"), false)
        );
    }
    
    @Nested
    @DisplayName("Price Range Filtering Tests")
    class PriceFilteringTests {
        
        @Test
        @DisplayName("Should filter by minimum price")
        void testMinimumPriceFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMinPrice(new BigDecimal("500"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("500")) >= 0,
                    "Product price should be >= 500: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter by maximum price")
        void testMaximumPriceFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMaxPrice(new BigDecimal("1000"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("1000")) <= 0,
                    "Product price should be <= 1000: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter by price range")
        void testPriceRangeFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMinPrice(new BigDecimal("100"));
            criteria.setMaxPrice(new BigDecimal("1000"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("100")) >= 0 &&
                          product.getPrice().compareTo(new BigDecimal("1000")) <= 0,
                    "Product price should be between 100-1000: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Category and Brand Filtering Tests")
    class CategoryBrandFilteringTests {
        
        @Test
        @DisplayName("Should filter by single category")
        void testSingleCategoryFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setCategories(Arrays.asList("ELECTRONICS"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertEquals("ELECTRONICS", product.getCategory(),
                    "Product should be in ELECTRONICS category: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter by multiple categories")
        void testMultipleCategoryFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setCategories(Arrays.asList("ELECTRONICS", "FASHION"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(Arrays.asList("ELECTRONICS", "FASHION").contains(product.getCategory()),
                    "Product should be in ELECTRONICS or FASHION: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter by brand")
        void testBrandFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setBrands(Arrays.asList("Apple", "Samsung"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(Arrays.asList("Apple", "Samsung").contains(product.getBrand()),
                    "Product should be Apple or Samsung: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Stock and Availability Filtering Tests")
    class StockFilteringTests {
        
        @Test
        @DisplayName("Should filter by in-stock only")
        void testInStockFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setInStockOnly(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getStockQuantity() > 0,
                    "Product should be in stock: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should include out-of-stock when not filtering")
        void testIncludeOutOfStock() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setInStockOnly(false);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            // Should include the Dell Laptop which has 0 stock
            boolean hasOutOfStock = results.stream()
                .anyMatch(p -> p.getStockQuantity() == 0);
            assertTrue(hasOutOfStock || results.size() == sampleProducts.size(),
                "Should include out-of-stock products when not filtering");
        }
    }
    
    @Nested
    @DisplayName("Rating and Quality Filtering Tests")
    class RatingFilteringTests {
        
        @Test
        @DisplayName("Should filter by minimum rating")
        void testMinimumRatingFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMinRating(4.3);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getRating() >= 4.3,
                    "Product rating should be >= 4.3: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter premium products")
        void testPremiumFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setPremiumOnly(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("100")) >= 0 && 
                          product.getRating() >= 4.0,
                    "Premium product should have price >= 100 and rating >= 4.0: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Discount and Sale Filtering Tests")
    class DiscountFilteringTests {
        
        @Test
        @DisplayName("Should filter by on-sale products")
        void testOnSaleFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setOnSaleOnly(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getDiscountPercentage() > 0,
                    "On-sale product should have discount > 0: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should include non-discounted when not filtering")
        void testIncludeNonDiscounted() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setOnSaleOnly(false);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            // Should include products without discounts
            boolean hasNonDiscounted = results.stream()
                .anyMatch(p -> p.getDiscountPercentage() == 0);
            assertTrue(hasNonDiscounted || results.size() == sampleProducts.size(),
                "Should include non-discounted products when not filtering");
        }
    }
    
    @Nested
    @DisplayName("Date and Freshness Filtering Tests")
    class DateFilteringTests {
        
        @Test
        @DisplayName("Should filter by new products")
        void testNewProductsFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setNewProductsSince(LocalDate.now().minusDays(20));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            LocalDate cutoffDate = LocalDate.now().minusDays(20);
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getCreatedDate().isEqual(cutoffDate) || 
                          product.getCreatedDate().isAfter(cutoffDate),
                    "New product should be created within 20 days: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Search and Tag Filtering Tests")
    class SearchFilteringTests {
        
        @Test
        @DisplayName("Should filter by keyword search")
        void testKeywordSearchFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setSearchKeyword("smartphone");
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getName().toLowerCase().contains("smartphone") ||
                          product.getDescription().toLowerCase().contains("smartphone"),
                    "Product should contain 'smartphone' in name or description: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter by tags")
        void testTagFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setTags(Arrays.asList("luxury", "mobile"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                boolean hasMatchingTag = product.getTags().stream()
                    .anyMatch(tag -> Arrays.asList("luxury", "mobile").contains(tag));
                assertTrue(hasMatchingTag,
                    "Product should have at least one matching tag: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Featured Products Filtering Tests")
    class FeaturedFilteringTests {
        
        @Test
        @DisplayName("Should filter by featured products")
        void testFeaturedFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setFeaturedOnly(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.isFeatured(),
                    "Featured product should be marked as featured: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Complex Business Rules Tests")
    class AdvancedFilteringTests {
        
        @Test
        @DisplayName("Should filter electronics on sale")
        void testElectronicsOnSale() {
            ProductFilterL.AdvancedFilterCriteria criteria = new ProductFilterL.AdvancedFilterCriteria();
            criteria.setElectronicsOnSale(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProductsAdvanced(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertEquals("ELECTRONICS", product.getCategory());
                assertTrue(product.getDiscountPercentage() > 0,
                    "Electronics on sale should have discount: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter high-value items")
        void testHighValueItems() {
            ProductFilterL.AdvancedFilterCriteria criteria = new ProductFilterL.AdvancedFilterCriteria();
            criteria.setHighValueItems(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProductsAdvanced(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("500")) >= 0,
                    "High-value item should have price >= 500: " + product.getName());
                assertTrue(product.getRating() >= 4.5,
                    "High-value item should have rating >= 4.5: " + product.getName());
                assertTrue(product.getStockQuantity() >= 10,
                    "High-value item should have stock >= 10: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter popular new arrivals")
        void testPopularNewArrivals() {
            ProductFilterL.AdvancedFilterCriteria criteria = new ProductFilterL.AdvancedFilterCriteria();
            criteria.setPopularNewArrivals(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProductsAdvanced(sampleProducts, criteria);
            
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getCreatedDate().isEqual(thirtyDaysAgo) || 
                          product.getCreatedDate().isAfter(thirtyDaysAgo),
                    "Popular new arrival should be created within 30 days: " + product.getName());
                assertTrue(product.getRating() >= 4.0,
                    "Popular new arrival should have rating >= 4.0: " + product.getName());
                assertTrue(product.getStockQuantity() >= 5,
                    "Popular new arrival should have stock >= 5: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter clearance items")
        void testClearanceItems() {
            ProductFilterL.AdvancedFilterCriteria criteria = new ProductFilterL.AdvancedFilterCriteria();
            criteria.setClearanceItems(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProductsAdvanced(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getDiscountPercentage() >= 30,
                    "Clearance item should have discount >= 30%: " + product.getName());
                assertTrue(product.getStockQuantity() <= 5,
                    "Clearance item should have stock <= 5: " + product.getName());
                assertFalse(product.isFeatured(),
                    "Clearance item should not be featured: " + product.getName());
            }
        }
        
        @Test
        @DisplayName("Should filter luxury items")
        void testLuxuryItems() {
            ProductFilterL.AdvancedFilterCriteria criteria = new ProductFilterL.AdvancedFilterCriteria();
            criteria.setLuxuryItems(true);
            
            List<ProductFilterL.Product> results = productFilter.filterProductsAdvanced(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("1000")) >= 0,
                    "Luxury item should have price >= 1000: " + product.getName());
                assertTrue(Arrays.asList("JEWELRY", "WATCHES", "FASHION").contains(product.getCategory()),
                    "Luxury item should be in luxury category: " + product.getName());
                assertTrue(product.getRating() >= 4.5,
                    "Luxury item should have rating >= 4.5: " + product.getName());
            }
        }
    }
    
    @Nested
    @DisplayName("Combined Filtering Tests")
    class CombinedFilteringTests {
        
        @Test
        @DisplayName("Should apply multiple criteria together")
        void testMultipleCriteriaFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMinPrice(new BigDecimal("500"));
            criteria.setCategories(Arrays.asList("ELECTRONICS"));
            criteria.setInStockOnly(true);
            criteria.setMinRating(4.0);
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            for (ProductFilterL.Product product : results) {
                assertTrue(product.getPrice().compareTo(new BigDecimal("500")) >= 0,
                    "Should meet price criteria");
                assertEquals("ELECTRONICS", product.getCategory(),
                    "Should meet category criteria");
                assertTrue(product.getStockQuantity() > 0,
                    "Should meet stock criteria");
                assertTrue(product.getRating() >= 4.0,
                    "Should meet rating criteria");
            }
        }
        
        @Test
        @DisplayName("Should return empty list when no products match")
        void testNoMatchingProducts() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            criteria.setMinPrice(new BigDecimal("10000")); // Very high price
            criteria.setCategories(Arrays.asList("NONEXISTENT"));
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            assertTrue(results.isEmpty(), "Should return empty list when no products match");
        }
        
        @Test
        @DisplayName("Should return all products when no criteria specified")
        void testNoCriteriaFiltering() {
            ProductFilterL.FilterCriteria criteria = new ProductFilterL.FilterCriteria();
            
            List<ProductFilterL.Product> results = productFilter.filterProducts(sampleProducts, criteria);
            
            assertEquals(sampleProducts.size(), results.size(),
                "Should return all products when no criteria specified");
        }
    }
}
