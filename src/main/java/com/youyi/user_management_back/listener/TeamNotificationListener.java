package com.youyi.user_management_back.listener;

import com.youyi.user_management_back.model.domain.TeamNotification;
import com.youyi.user_management_back.service.TeamNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TeamNotificationListener {

    @Resource
    private TeamNotificationService notificationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "quit.queue",durable = "true"),
            exchange = @Exchange(name = "team.direct"),
            key = "quit"
    ))
    public void listenTeamQueue(TeamNotification ntf) {
        if (ntf == null) {
            return;
        }
        boolean save = notificationService.save(ntf);
        if (!save) {
            log.error(ntf + "消息处理失败");
        }
    }


}
