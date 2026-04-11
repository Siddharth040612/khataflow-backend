package com.khataflow.service;

import com.khataflow.entity.Party;
import com.khataflow.entity.Transaction;
import com.khataflow.entity.User;
import com.khataflow.exception.NotFoundException;
import com.khataflow.repository.PartyRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final PartyRepository partyRepository;

    public ValidationService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    // 🔥 Party validation
    public Party validateParty(Long partyId, Long storeId) {
        return partyRepository.findByIdAndStoreId(partyId, storeId)
                .orElseThrow(() -> new NotFoundException("Invalid party for this store"));
    }

//    public User validateUser(Long userId, Long storeId) {
//        return userRepository.findByIdAndStoreId(userId, storeId)
//                .orElseThrow(() -> new NotFoundException("Invalid user"));
//    }
//
//    public Transaction validateTransaction(Long txnId, Long storeId) {
//        return txnRepository.findByIdAndStoreId(txnId, storeId)
//                .orElseThrow(() -> new NotFoundException("Invalid transaction"));
//    }
}