package com.dozycoffee.infrastructure.adapter;

import com.dozycoffee.branch.application.BranchRepository;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.product.application.repository.BranchExistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BranchExistenceAdapter implements BranchExistencePort {

    private final BranchRepository branchRepository;

    @Override
    public boolean existsById(BranchId branchId) throws RepositoryException {
        return branchRepository.findById(branchId).isPresent();
    }
}
