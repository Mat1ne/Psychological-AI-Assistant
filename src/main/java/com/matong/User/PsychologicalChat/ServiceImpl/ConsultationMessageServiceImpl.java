package com.matong.User.PsychologicalChat.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matong.User.PsychologicalChat.Entity.ConsultationMessage;
import com.matong.User.PsychologicalChat.Mapper.ConsultationMessageMapper;
import com.matong.User.PsychologicalChat.Service.ConsultationMessageService;
import com.matong.User.PsychologicalChat.VO.ConsultationMessageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationMessageServiceImpl implements ConsultationMessageService {
    private final ConsultationMessageMapper consultationMessageMapper;

    /**
      * 存储用户初始信息
     * @param sessionId
     * @param content
     * @param emotion_tag
     * @return
     */
    @Override
    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotion_tag) {
        log.info("保存用户会话记录: sessionId={}, content={}, emotion_tag={}", sessionId, content, emotion_tag);
        //构建用户消息实体
        ConsultationMessage userMessage = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotion_tag)
                .aiModel("OpenAI")
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(userMessage);
        return userMessage;
    }


    /**
     * 获取用户会话记录数量
     * @param sessionId
     * @return
     */
    @Override
    public Integer getMessageCountBySessionId(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId);
        Long count = consultationMessageMapper.selectCount(queryWrapper);
        return count.intValue();
    }

    /**
     * 获取用户最后一条信息
     * @param sessionId
     * @return
     */
    @Override
    public ConsultationMessageResponseDTO getLastMessageBySessionId(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .orderByDesc(ConsultationMessage::getCreatedAt)
                .last("limit 1");
        ConsultationMessage lastMessage = consultationMessageMapper.selectOne(queryWrapper);
        return lastMessage != null ? ConsultationMessageServiceImpl.convertToResponseDTO(lastMessage) : null;
    }

    /**
     * 保存ai信息
     * @param sessionId
     * @param completeRes
     * @param openai
     * @return
     */
    @Override
    public ConsultationMessage saveAiMessage(Long sessionId, String completeRes, String openai) {
        log.info("保存AI助手会话记录: sessionId={}, completeRes={}, openai={}", sessionId, completeRes, openai);
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)
                .messageType(1)
                .content(completeRes)
                .aiModel(openai)
                .createdAt(LocalDateTime.now())
                .build();
        // 插入数据库
        consultationMessageMapper.insert(message);
        return message;
    }

    /**
    * 将ConsultationMessage实体转换为ConsultationMessageResponseDTO
     * @param message
     * @return
     */
    private static ConsultationMessageResponseDTO convertToResponseDTO(ConsultationMessage message) {
        if (message == null) {
            return null;
        }

        // 手动逐字段赋值，确保转换的准确性和可控性
        ConsultationMessageResponseDTO responseDTO = new ConsultationMessageResponseDTO();
        responseDTO.setId(message.getId());
        responseDTO.setSessionId(message.getSessionId());
        responseDTO.setSenderType(message.getSenderType());
        responseDTO.setMessageType(message.getMessageType());
        responseDTO.setContent(message.getContent());
        responseDTO.setEmotionTag(message.getEmotionTag());
        responseDTO.setAiModel(message.getAiModel());
        responseDTO.setCreatedAt(message.getCreatedAt());

        // 设置描述字段（通过实体方法获取）
        responseDTO.setSenderTypeDesc(message.getSenderTypeDesc());
        responseDTO.setMessageTypeDesc(message.getMessageTypeDesc());

        // 计算消息长度
        responseDTO.calculateContentLength();

        return responseDTO;
    }

}
