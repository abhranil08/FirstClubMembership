package com.firstclub.membership.controller;

import com.firstclub.membership.dto.MembershipPlanDto;
import com.firstclub.membership.dto.MembershipTierDto;
import com.firstclub.membership.service.MembershipCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class MembershipCatalogController {

    private final MembershipCatalogService catalogService;

    @GetMapping("/plans")
    public ResponseEntity<List<MembershipPlanDto>> getPlans() {
        return ResponseEntity.ok(catalogService.getAllPlans());
    }

    @GetMapping("/tiers")
    public ResponseEntity<List<MembershipTierDto>> getTiers() {
        return ResponseEntity.ok(catalogService.getAllTiers());
    }
}
