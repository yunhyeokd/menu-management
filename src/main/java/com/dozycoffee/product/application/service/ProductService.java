package com.dozycoffee.product.application.service;

import com.dozycoffee.branch.application.BranchRepository;
import com.dozycoffee.core.domain.IdentifierGenerator;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.ProductServiceCode;
import com.dozycoffee.core.application.exception.*;
import com.dozycoffee.product.application.dto.*;
import com.dozycoffee.product.application.repository.*;
import com.dozycoffee.product.application.service.tag.TagService;
import com.dozycoffee.product.domain.*;

import java.util.*;

public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTagRepository productTagRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final BranchRepository branchAccountRepository;
    private final IdentifierGenerator<ProductId> idGenerator;
    private final TagService tagService;

    public ProductService(ProductRepository productRepository, ProductQueryRepository productQueryRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ProductTagRepository productTagRepository, OptionGroupRepository optionGroupRepository, ProductOptionGroupRepository productOptionGroupRepository, BranchRepository branchAccountRepository, IdentifierGenerator<ProductId> idGenerator, TagService tagService) {
        this.productRepository = productRepository;
        this.productQueryRepository = productQueryRepository;
        this.categoryRepository = categoryRepository;
        this.productTagRepository = productTagRepository;
        this.optionGroupRepository = optionGroupRepository;
        this.productOptionGroupRepository = productOptionGroupRepository;
        this.branchAccountRepository = branchAccountRepository;
        this.idGenerator = idGenerator;
        this.tagService = tagService;
    }

    /*
    Product를 repository에서 불러온다
    !! Product 미존재 시 예외 발생
     */
    private Product getProduct(ProductId id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.PRODUCT_NOT_FOUND_ERROR));
    }

    /*
    문자열 Set을 입력받아 Tag를 Product에 연결
    문자열과 일치하는 Tag가 없으면 새로 생성
    * Product ID에 대한 Product 존재 여부 검증은 앞에서 수행했다고 가정
    !! 부적절한 태그 입력 시 예외 발생
     */
    private List<TagData> saveTags(ProductId productId, Set<String> tagNames) {
        List<TagData> tags = tagNames.stream()
                .map(tagService::findOrCreate)
                .map(TagData::from)
                .toList();
        for (TagData tag : tags) {
            productTagRepository.save(ProductTag.create(productId, tag.id()));
        }
        return tags;
    }

    /*
    문자열 Set을 입력받아 Product의 모든 태그를 Set의 문자열에 담긴 태그로 교체
    문자열과 일치하는 Tag가 없으면 새로 생성
    !! 부적절한 태그 입력 시 예외 발생
     */
    private List<TagData> replaceProductTags(ProductId productId, Set<String> tagNames) {
        productTagRepository.deleteAllByProductId(productId);
        return saveTags(productId, tagNames);
    }

    /*
    Product와 OptionGroup의 연결 정보들을 생성하고 저장
    * Product ID에 대한 Product 존재 여부 검증은 앞에서 수행했다고 가정
    !! 해당하는 OptionGroup 미존재 시 예외 발생
     */
    private void saveOptionGroups(ProductId productId, List<OptionGroupLinkSpec> specs) {
        for (OptionGroupLinkSpec spec : specs) {
            optionGroupRepository.findById(spec.optionGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.OPTION_GROUP_NOT_FOUND_ERROR));
            productOptionGroupRepository.save(ProductOptionGroup.create(
                    productId,
                    spec.optionGroupId(),
                    spec.isRequired(),
                    spec.allowMultiple()
            ));
        }
    }

    /*
    Product와 OptionGroup의 기존 연결 정보들을 제거하고 새로운 연결 정보들을 생성
    !! 해당하는 OptionGroup 미존재 시 예외 발생
     */
    public void replaceOptionGroups(ProductId productId, List<OptionGroupLinkSpec> specs) {
        try {
            getProduct(productId);
            productOptionGroupRepository.deleteAllByProductId(productId);
            saveOptionGroups(productId, specs);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public ProductData registerCommonProduct(CommonProductRegisterCommand command) {
        try {
            categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            ProductId productId = idGenerator.generate();
            Product product = Product.createCommonProduct(
                    productId,
                    command.name(),
                    command.description(),
                    command.imageUrl(),
                    command.categoryId(),
                    command.price(),
                    command.kcal(),
                    command.allergenInfo()
            );
            productRepository.save(product);
            List<TagData> tags = saveTags(productId, command.tagNames());
            saveOptionGroups(productId, command.optionGroups());
            return ProductData.from(product, tags);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public ProductData registerBranchProduct(BranchProductRegisterCommand command) {
        try {
            categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            branchAccountRepository.findById(command.branchId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.BRANCH_NOT_FOUND_ERROR));

            ProductId productId = idGenerator.generate();
            Product product = Product.createBranchProduct(
                    productId,
                    command.name(),
                    command.description(),
                    command.imageUrl(),
                    command.categoryId(),
                    command.price(),
                    command.kcal(),
                    command.allergenInfo(),
                    command.branchId()
            );
            productRepository.save(product);
            List<TagData> tags = saveTags(productId, command.tagNames());
            saveOptionGroups(productId, command.optionGroups());
            return ProductData.from(product, tags);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public List<ProductDetailResult> searchProducts(ProductFilterQuery query) {
        try {
            return productQueryRepository.findByFilter(query);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public ProductData updateProfile(ProductProfileUpdateCommand command) {
        try {
            Product product = getProduct(command.id());
            categoryRepository
                    .findById(command.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(ProductServiceCode.PRD, ProductErrors.CATEGORY_NOT_FOUND_ERROR));
            product.updateName(command.name());
            product.updateDescription(command.description());
            product.updateImageUrl(command.imageUrl());
            product.changeCategory(command.categoryId());
            product.updatePrice(command.price());
            product.updateKcal(command.kcal());
            product.updateAllergenInfo(command.allergenInfo());

            productRepository.save(product);
            List<TagData> tags = replaceProductTags(product.getId(), command.tags());
            return ProductData.from(product, tags);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deleteProduct(ProductId productId) {
        try {
            getProduct(productId);
            productTagRepository.deleteAllByProductId(productId);
            productOptionGroupRepository.deleteAllByProductId(productId);
            productRepository.deleteById(productId);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void activate(ProductId productId) {
        try {
            Product product = getProduct(productId);
            product.activate();
            productRepository.save(product);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

    public void deactivate(ProductId productId) {
        try {
            Product product = getProduct(productId);
            product.deactivate();
            productRepository.save(product);
        } catch (ProductException e) {
            throw new ValidationException(ProductServiceCode.PRD, ProductErrors.INVALID_PRODUCT_ERROR);
        } catch (RepositoryException e) {
            throw new SystemException(ProductServiceCode.PRD, ProductErrors.UNKNOWN_ERROR);
        }
    }

}
