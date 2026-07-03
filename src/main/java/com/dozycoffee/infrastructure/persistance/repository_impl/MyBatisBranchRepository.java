package com.dozycoffee.infrastructure.persistance.repository_impl;

import com.dozycoffee.branch.application.BranchRepository;
import com.dozycoffee.branch.domain.Branch;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.branch.domain.BranchId;
import com.dozycoffee.core.application.RepositoryException;
import com.dozycoffee.infrastructure.persistance.entity.BranchRow;
import com.dozycoffee.infrastructure.persistance.mapper.BranchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisBranchRepository implements BranchRepository {

    private final BranchMapper branchMapper;

    @Override
    public void save(Branch branch) throws RepositoryException {
        branchMapper.upsert(RowMapper.toRow(branch));
    }

    @Override
    public Optional<Branch> findById(BranchId branchId) throws RepositoryException {
        return branchMapper.findById(branchId.getValue()).map(BranchRow::toBranch);
    }

    @Override
    public Optional<Branch> findByCode(BranchCode code) throws RepositoryException {
        return branchMapper.findByCode(code.getValue()).map(BranchRow::toBranch);
    }

    @Override
    public Optional<Branch> findByName(String name) throws RepositoryException {
        return branchMapper.findByName(name).map(BranchRow::toBranch);
    }

    @Override
    public List<Branch> findAll() throws RepositoryException {
        return branchMapper.findAll().stream().map(BranchRow::toBranch).toList();
    }

    @Override
    public void deleteById(BranchId branchId) throws RepositoryException {
        branchMapper.deleteById(branchId.getValue());
    }

    public static class RowMapper {
        public static BranchRow toRow(Branch branch) {
            return new BranchRow(
                    branch.getId().getValue(),
                    branch.getCode().getValue(),
                    branch.getAuthKeyHash(),
                    branch.getStatus().name(),
                    branch.getCreatedAt(),
                    branch.getDeletedAt(),
                    branch.getName(),
                    branch.getAddress()
            );
        }
    }
}
