package com.khataflow.service;

import com.khataflow.entity.Party;
import com.khataflow.entity.PartyType;
import com.khataflow.repository.PartyRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.khataflow.dto.PartyUpdateRequest;
import org.springframework.data.domain.*;

import java.util.List;

@Service
public class PartyService {

    private final PartyRepository repository;

    public PartyService(PartyRepository repository) {
        this.repository = repository;
    }

    public Party create(Party party) {
        // Temporary user ID (later from auth)
        party.setCreatedBy(1L);
        return repository.save(party);
    }

    public List<Party> getAll(Long storeId) {
        return repository.findByStoreId(storeId);
    }

    public Page<Party> search(
            Long storeId,
            String name,
            String phone,
            String externalId,
            Long id,
            PartyType partyType,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Party> spec = (root, query, cb) ->
                cb.equal(root.get("storeId"), storeId);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (phone != null && !phone.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("phone"), "%" + phone + "%"));
        }

        if (externalId != null && !externalId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("externalId"), externalId));
        }

        if (id != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id"), id));
        }

        // 🔥 NEW: partyType filter
        if (partyType != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("partyType"), partyType));
        }

        return repository.findAll(spec, pageable);
    }
    public Party update(Long id, Long storeId, PartyUpdateRequest request) {

        Party existing = repository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        if (request.getName() != null)
            existing.setName(request.getName());

        if (request.getPhone() != null)
            existing.setPhone(request.getPhone());

        if (request.getEmail() != null)
            existing.setEmail(request.getEmail());

        if (request.getPartyType() != null)
            existing.setPartyType(request.getPartyType());

        if (request.getExternalId() != null)
            existing.setExternalId(request.getExternalId());

        existing.setUpdatedBy(1L);

        return repository.save(existing);
    }


    public void delete(Long id, Long storeId) {

        Party party = repository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        repository.delete(party);
    }
}