package com.matong.Admin.PsychologicalChat.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matong.Admin.PsychologicalChat.Entity.ConsultationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultationMessageMapper extends BaseMapper<ConsultationMessage> {
}
