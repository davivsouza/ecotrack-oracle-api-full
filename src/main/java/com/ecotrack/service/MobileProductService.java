package com.ecotrack.service;

import com.ecotrack.client.OpenFoodFactsClient;
import com.ecotrack.domain.Product;
import com.ecotrack.domain.ProductImpact;
import com.ecotrack.domain.ProductNutrition;
import com.ecotrack.dto.mobile.EnvironmentalImpactResponse;
import com.ecotrack.dto.mobile.NutritionalInfoResponse;
import com.ecotrack.dto.mobile.ProductResponse;
import com.ecotrack.exception.ResourceNotFoundException;
import com.ecotrack.repository.ProductImpactRepository;
import com.ecotrack.repository.ProductNutritionRepository;
import com.ecotrack.repository.ProductRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MobileProductService {
  private static final int MAX_NUTRI_VALUE_LENGTH = 120;

  private final ProductRepository productRepository;
  private final ProductImpactRepository productImpactRepository;
  private final ProductNutritionRepository productNutritionRepository;
  private final OpenFoodFactsClient openFoodFactsClient;
  private final ExternalIdCodec externalIdCodec;

  public MobileProductService(
    ProductRepository productRepository,
    ProductImpactRepository productImpactRepository,
    ProductNutritionRepository productNutritionRepository,
    OpenFoodFactsClient openFoodFactsClient,
    ExternalIdCodec externalIdCodec
  ) {
    this.productRepository = productRepository;
    this.productImpactRepository = productImpactRepository;
    this.productNutritionRepository = productNutritionRepository;
    this.openFoodFactsClient = openFoodFactsClient;
    this.externalIdCodec = externalIdCodec;
  }

  public List<ProductResponse> list(String search) {
    String term = normalize(search);

    return productRepository.findAll().stream()
      .filter(product -> matchesSearch(product, term))
      .map(product -> toProductResponse(product, "local"))
      .collect(Collectors.toList());
  }

  public ProductResponse getById(String id) {
    UUID productId = externalIdCodec.parseProductId(id);
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
    return toProductResponse(product, "local");
  }

  @Transactional
  public ProductResponse getByBarcode(String barcode) {
    Optional<Product> local = productRepository.findByBarcode(barcode);
    if (local.isPresent()) {
      return toProductResponse(local.get(), "local");
    }

    Product imported = importFromOpenFoodFacts(barcode)
      .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

    return toProductResponse(imported, "open-food-facts");
  }

  public Product getEntityByExternalId(String externalId) {
    UUID productId = externalIdCodec.parseProductId(externalId);
    return productRepository.findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
  }

  public ProductResponse toProductResponse(Product product, String source) {
    List<ProductNutrition> nutritionRows = productNutritionRepository.findByProduct(product);
    Map<String, String> nutrition = nutritionRows.stream()
      .collect(Collectors.toMap(ProductNutrition::getNutriKey, ProductNutrition::getNutriValue, (left, right) -> right, LinkedHashMap::new));

    ProductImpact impact = productImpactRepository.findById(product.getId()).orElse(null);

    BigDecimal calories = firstNonNull(product.getKcal100g(), decimal(nutrition.get("calories")));
    BigDecimal protein = decimal(nutrition.get("protein"));
    BigDecimal carbs = decimal(nutrition.get("carbs"));
    BigDecimal fat = decimal(nutrition.get("fat"));
    BigDecimal sugar = decimal(nutrition.get("sugar"));
    BigDecimal sodium = decimal(nutrition.get("sodium"));
    BigDecimal fiber = decimal(nutrition.get("fiber"));

    BigDecimal carbon = firstNonNull(
      product.getCo2PerUnit(),
      impact == null ? null : impact.getCo2PerUnit(),
      decimal(nutrition.get("carbon_footprint"))
    );

    BigDecimal water = firstNonNull(
      impact == null ? null : impact.getWaterL(),
      decimal(nutrition.get("water_usage"))
    );

    int healthScore = computeHealthScore(calories, protein, carbs, fat, sugar, sodium, fiber);
    int sustainabilityScore = computeSustainabilityScore(carbon, water);

    NutritionalInfoResponse nutritionalInfo = new NutritionalInfoResponse(
      nvl(calories),
      nvl(protein),
      nvl(carbs),
      nvl(fat),
      nvl(sugar),
      nvl(sodium),
      nvl(fiber)
    );

    EnvironmentalImpactResponse environmentalImpact = new EnvironmentalImpactResponse(
      nvl(carbon),
      nvl(water),
      nutrition.getOrDefault("packaging_type", "não informado"),
      sustainabilityScore,
      nutrition.get("eco_score_grade")
    );

    List<String> categories = parseCategories(product.getCategory());
    String ingredients = nutrition.get("meta_ingredients");

    int dataCompleteness = calculateCompleteness(calories, carbon, water, ingredients, categories, nutrition.get("meta_image"));

    return new ProductResponse(
      externalIdCodec.productId(product.getId()),
      product.getName(),
      nutrition.getOrDefault("meta_brand", "EcoTrack"),
      product.getBarcode(),
      nutrition.get("meta_image"),
      ingredients,
      categories,
      nutrition.get("meta_nutri_score"),
      nutritionalInfo,
      environmentalImpact,
      healthScore,
      sustainabilityScore,
      Collections.emptyList(),
      source,
      dataCompleteness
    );
  }

  private Optional<Product> importFromOpenFoodFacts(String barcode) {
    try {
      Map<String, Object> payload = openFoodFactsClient.findByBarcode(barcode);
      Map<String, Object> productData = map(payload.get("product"));
      if (productData.isEmpty()) {
        return Optional.empty();
      }

      String name = asString(productData.get("product_name"));
      if (name == null || name.isBlank()) {
        name = asString(productData.get("product_name_en"));
      }
      if (name == null || name.isBlank()) {
        return Optional.empty();
      }

      String categoriesRaw = asString(productData.get("categories"));
      String category = parsePrimaryCategory(categoriesRaw);
      String normalizedBarcode = normalizeBarcode(asString(productData.get("code")), barcode);

      Product product = Product.builder()
        .id(UUID.randomUUID())
        .name(name.trim())
        .category(category)
        .barcode(normalizedBarcode)
        .kcal100g(extractNutriment(productData, "energy-kcal_100g", "energy_kcal_100g"))
        .co2PerUnit(extractNutriment(productData, "carbon-footprint_100g"))
        .build();

      Product saved = productRepository.save(product);

      String brand = asString(productData.get("brands"));
      String image = asString(productData.get("image_url"));
      String ingredients = asString(productData.get("ingredients_text"));
      String nutriScore = asString(productData.get("nutriscore_grade"));

      saveNutrition(saved, "meta_brand", safe(brand));
      saveNutrition(saved, "meta_image", safe(image));
      saveNutrition(saved, "meta_ingredients", safe(ingredients));
      saveNutrition(saved, "meta_nutri_score", safe(nutriScore));

      saveNumericNutrition(saved, "calories", extractNutriment(productData, "energy-kcal_100g", "energy_kcal_100g"));
      saveNumericNutrition(saved, "protein", extractNutriment(productData, "proteins_100g"));
      saveNumericNutrition(saved, "carbs", extractNutriment(productData, "carbohydrates_100g"));
      saveNumericNutrition(saved, "fat", extractNutriment(productData, "fat_100g"));
      saveNumericNutrition(saved, "sugar", extractNutriment(productData, "sugars_100g"));
      saveNumericNutrition(saved, "sodium", extractNutriment(productData, "sodium_100g", "salt_100g"));
      saveNumericNutrition(saved, "fiber", extractNutriment(productData, "fiber_100g"));

      BigDecimal water = extractNutriment(productData, "water_100g");
      BigDecimal carbon = firstNonNull(product.getCo2PerUnit(), extractNutriment(productData, "carbon-footprint_100g"));

      productImpactRepository.save(ProductImpact.builder()
        .productId(saved.getId())
        .co2PerUnit(carbon)
        .waterL(water)
        .origin("OPEN_FOOD_FACTS")
        .updatedAt(java.time.OffsetDateTime.now())
        .build());

      return Optional.of(saved);
    } catch (FeignException.NotFound ex) {
      return Optional.empty();
    } catch (FeignException ex) {
      return Optional.empty();
    }
  }

  private void saveNutrition(Product product, String key, String value) {
    if (value == null || value.isBlank()) {
      return;
    }

    ProductNutrition row = ProductNutrition.builder()
      .id(UUID.randomUUID())
      .product(product)
      .nutriKey(key)
      .nutriValue(limit(value, MAX_NUTRI_VALUE_LENGTH))
      .build();
    productNutritionRepository.save(row);
  }

  private void saveNumericNutrition(Product product, String key, BigDecimal value) {
    if (value == null) {
      return;
    }
    saveNutrition(product, key, value.setScale(2, RoundingMode.HALF_UP).toPlainString());
  }

  private BigDecimal extractNutriment(Map<String, Object> productData, String... keys) {
    Map<String, Object> nutriments = map(productData.get("nutriments"));
    for (String key : keys) {
      BigDecimal value = decimal(nutriments.get(key));
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private int computeHealthScore(
    BigDecimal calories,
    BigDecimal protein,
    BigDecimal carbs,
    BigDecimal fat,
    BigDecimal sugar,
    BigDecimal sodium,
    BigDecimal fiber
  ) {
    double score = 100d;
    score -= number(calories) / 5d;
    score -= number(sugar) * 2d;
    score -= number(sodium) * 10d;
    score -= number(fat);
    score += number(protein) * 2d;
    score += number(fiber) * 2d;
    return clampScore(score);
  }

  private int computeSustainabilityScore(BigDecimal carbon, BigDecimal water) {
    double score = 100d;
    score -= number(carbon) * 20d;
    score -= number(water) * 0.05d;
    return clampScore(score);
  }

  private int clampScore(double score) {
    if (score < 0) {
      return 0;
    }
    if (score > 100) {
      return 100;
    }
    return (int) Math.round(score);
  }

  private int calculateCompleteness(
    BigDecimal calories,
    BigDecimal carbon,
    BigDecimal water,
    String ingredients,
    List<String> categories,
    String image
  ) {
    int total = 6;
    int ok = 0;
    if (calories != null) ok++;
    if (carbon != null) ok++;
    if (water != null) ok++;
    if (ingredients != null && !ingredients.isBlank()) ok++;
    if (categories != null && !categories.isEmpty()) ok++;
    if (image != null && !image.isBlank()) ok++;
    return (int) Math.round((ok * 100.0) / total);
  }

  private boolean matchesSearch(Product product, String term) {
    if (term.isBlank()) {
      return true;
    }

    Map<String, String> nutrition = productNutritionRepository.findByProduct(product).stream()
      .collect(Collectors.toMap(ProductNutrition::getNutriKey, ProductNutrition::getNutriValue, (left, right) -> right, LinkedHashMap::new));

    String name = normalize(product.getName());
    String category = normalize(product.getCategory());
    String barcode = normalize(product.getBarcode());
    String brand = normalize(nutrition.get("meta_brand"));
    String ingredients = normalize(nutrition.get("meta_ingredients"));

    return name.contains(term)
      || category.contains(term)
      || barcode.contains(term)
      || brand.contains(term)
      || ingredients.contains(term);
  }

  private List<String> parseCategories(String category) {
    if (category == null || category.isBlank()) {
      return Collections.emptyList();
    }

    String[] parts = category.split(",");
    List<String> categories = new ArrayList<>();
    for (String part : parts) {
      String normalized = part == null ? "" : part.trim();
      if (!normalized.isBlank()) {
        categories.add(normalized);
      }
    }
    return categories;
  }

  private String parsePrimaryCategory(String categories) {
    List<String> parsed = parseCategories(categories);
    if (!parsed.isEmpty()) {
      return parsed.get(0);
    }
    return "Geral";
  }

  private String normalizeBarcode(String barcodeFromApi, String fallbackBarcode) {
    String barcode = barcodeFromApi == null || barcodeFromApi.isBlank() ? fallbackBarcode : barcodeFromApi;
    return barcode == null ? null : barcode.trim();
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private String asString(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> map(Object value) {
    if (value instanceof Map<?, ?> source) {
      Map<String, Object> result = new LinkedHashMap<>();
      source.forEach((k, v) -> result.put(String.valueOf(k), v));
      return result;
    }
    return Collections.emptyMap();
  }

  private BigDecimal decimal(Object raw) {
    if (raw == null) {
      return null;
    }

    try {
      return new BigDecimal(String.valueOf(raw).replace(",", "."));
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private BigDecimal nvl(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private BigDecimal firstNonNull(BigDecimal... values) {
    for (BigDecimal value : values) {
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private double number(BigDecimal value) {
    return value == null ? 0d : value.doubleValue();
  }

  private String safe(String value) {
    return value == null ? null : value.trim();
  }

  private String limit(String value, int max) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.length() <= max ? normalized : normalized.substring(0, max);
  }
}
