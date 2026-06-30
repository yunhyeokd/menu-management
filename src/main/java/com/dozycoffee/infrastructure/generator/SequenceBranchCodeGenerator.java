package com.dozycoffee.infrastructure.generator;

import com.dozycoffee.branch.application.BranchCodeGenerator;
import com.dozycoffee.branch.domain.BranchCode;
import com.dozycoffee.infrastructure.persistance.mapper.BranchCodeSequenceMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Component
public class SequenceBranchCodeGenerator implements BranchCodeGenerator {

    private final BranchCodeSequenceMapper sequenceMapper;

    public SequenceBranchCodeGenerator(BranchCodeSequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    @Override
    @Transactional
    public BranchCode generate() {
        int year = Year.now().getValue();
        sequenceMapper.initializeIfAbsent(year);
        int seq = sequenceMapper.lockAndGetSeq(year);
        sequenceMapper.incrementSeq(year);
        return BranchCode.of(String.format("%04d%04d", year, seq));
    }
}
