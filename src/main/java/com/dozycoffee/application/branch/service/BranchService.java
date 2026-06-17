package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.branch.repository.BranchRepository;
import com.dozycoffee.application.product.repository.ProductRepository;

public class BranchService {

    private final int AUTH_KEY_LENGTH = 32;

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public BranchService(
            BranchRepository branchRepository,
            ProductRepository productRepository
    ) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }


}
