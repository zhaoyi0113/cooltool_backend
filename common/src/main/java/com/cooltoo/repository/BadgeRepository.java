package com.cooltoo.repository;

import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.entities.BadgeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 2/24/16.
 */
public interface BadgeRepository extends JpaRepository<BadgeEntity, Integer> {
    Page<BadgeEntity> findByAbilityType(SocialAbilityType abilityType, Pageable page);
    List<BadgeEntity> findByAbilityIdAndAbilityType(Integer abilityId, SocialAbilityType abilityType);
    List<BadgeEntity> findByAbilityIdAndAbilityType(Integer abilityId, SocialAbilityType abilityType, Sort sort);
    List<BadgeEntity> findByAbilityIdAndAbilityTypeAndGrade(Integer abilityId, SocialAbilityType abilityType, int grade);
    long countByAbilityType(SocialAbilityType abilityType);
}
