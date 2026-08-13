package com.matong.Admin.PsychologicalChat.ServiceImpl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.matong.Admin.Login.Entity.User;
import com.matong.Admin.Login.LoginMapper;
import com.matong.Admin.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.Admin.PsychologicalChat.Entity.ConsultationSession;
import com.matong.Admin.PsychologicalChat.Mapper.ConsultationSessionMapper;
import com.matong.Admin.PsychologicalChat.Service.ConsultationSessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationSessionServiceImpl implements ConsultationSessionService {
    private final LoginMapper loginMapper;
    private final ConsultationSessionMapper consultationSessionMapper;
    @Override
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        log.info("创建会话，userId：{}，createDTO：{}", userId, createDTO);
        User user = loginMapper.selectById(userId);
        if(user != null) {
            //创建会话记录
            ConsultationSession session  = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            if(StrUtil.isBlank(session.getSessionTitle())) {
                session.setSessionTitle(String.format("宁镀AI助手 -" + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm:ss")));
            }
            //插入会话记录
            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }
}
