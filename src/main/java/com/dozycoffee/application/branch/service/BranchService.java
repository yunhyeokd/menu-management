package com.dozycoffee.application.branch.service;

import com.dozycoffee.application.branch.dto.BranchProfileUpdateDto;
import com.dozycoffee.application.branch.repository.BranchProfileRepository;
import com.dozycoffee.application.common.RepositoryException;
import com.dozycoffee.application.product.repository.ProductRepository;
import com.dozycoffee.domain.branch.BranchException;
import com.dozycoffee.domain.branch.BranchProfile;

public class BranchService {

    private final BranchProfileRepository branchProfileRepository;
    private final ProductRepository productRepository;

    public BranchService(
            BranchProfileRepository branchProfileRepository,
            ProductRepository productRepository
    ) {
        this.branchProfileRepository = branchProfileRepository;
        this.productRepository = productRepository;
    }

    public void updateProfile(long branchId, BranchProfileUpdateDto updateDto) {

        try {
            BranchProfile branchProfile = branchProfileRepository.findById(branchId);
            if (branchProfile == null) {
                throw BranchBusinessException.with(BranchErrors.NOT_FOUND_ERROR);
            }
            try {
                branchProfile.changeName(updateDto.name());
                branchProfile.changeAddress(updateDto.address());
                branchProfileRepository.save(branchProfile);
            } catch (BranchException e) {
                throw BranchBusinessException.with(BranchErrors.INVALID_BRANCH_ERROR);
            }
        } catch (RepositoryException e) {
            throw BranchBusinessException.with(BranchErrors.UNKNOWN_ERROR);
        }

    }

}
