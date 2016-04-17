package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSkillEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yzzhao on 3/13/16.
 */
public interface NurseSkillRepository extends JpaRepository<NurseSkillEntity, Integer>{

    List<NurseSkillEntity> findByUserId(long userId, Sort sort);
    NurseSkillEntity findByUserIdAndSkillId(long userId, int skillId);
    void deleteByUserIdIn(List<Long> userIds);
    void deleteBySkillIdIn(List<Integer> skillIds);

}
