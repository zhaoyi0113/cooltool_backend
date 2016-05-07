package com.cooltoo.repository;

import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.entities.BadgeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yzzhao on 2/24/16.
 */
public interface BadgeRepository extends JpaRepository<BadgeEntity, Integer> {
    Page<BadgeEntity> findByAbilityType(SocialAbilityType abilityType, Pageable page);
    List<BadgeEntity> findByAbilityIdAndAbilityType(Integer abilityId, SocialAbilityType abilityType);
    List<BadgeEntity> findByAbilityIdAndAbilityType(Integer abilityId, SocialAbilityType abilityType, Sort sort);
    List<BadgeEntity> findByAbilityIdAndAbilityTypeAndGrade(Integer abilityId, SocialAbilityType abilityType, int grade);
    @Query("FROM BadgeEntity be WHERE be.point<=?1 AND be.abilityId=?2 AND be.abilityType=?3 ORDER BY be.point DESC")
    List<BadgeEntity> findOneByPoint(Long point, Integer abilityId, SocialAbilityType abilityType);
    long countByAbilityType(SocialAbilityType abilityType);
}
