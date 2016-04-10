package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseOccupationSkillEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseOccupationSkillRepository extends JpaRepository<NurseOccupationSkillEntity, Integer>{

    List<NurseOccupationSkillEntity> findByUserId(long userId, Sort sort);
    NurseOccupationSkillEntity       findByUserIdAndSkillId(long userId, int skillId);
    void deleteByUserIdIn(List<Long> userIds);
    void deleteBySkillIdIn(List<Integer> skillIds);

}
