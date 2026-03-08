package com.cl.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.TongzhijiluEntity;
import com.cl.utils.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class NotificationSendService {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Autowired
    private TongzhijiluService tongzhijiluService;

    @Autowired
    private NotificationUtil notificationUtil;

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public void sendPendingNotifications() {
        try {
            EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("songzhuangtai", 0);
            wrapper.or().eq("songzhuangtai", 2);
            List<JiuzhentongzhiEntity> pendingNotifications = jiuzhentongzhiService.selectList(wrapper);

            for (JiuzhentongzhiEntity notification : pendingNotifications) {
                sendNotification(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(JiuzhentongzhiEntity notification) {
        try {
            String content = buildNotificationContent(notification);
            boolean sendSuccess = false;
            String errorMessage = "";

            if (notificationUtil.isPhoneValid(notification.getShouji())) {
                sendSuccess = notificationUtil.sendNotification(notification.getShouji(), notification.getZhanghao(), content);
            } else {
                errorMessage = "手机号格式不正确";
            }

            TongzhijiluEntity record = new TongzhijiluEntity();
            record.setTongzhiid(notification.getId());
            record.setZhanghao(notification.getZhanghao());
            record.setShouji(notification.getShouji());
            record.setTongzhineirong(content);
            record.setAddtime(new Date());

            if (sendSuccess) {
                notification.setSongzhuangtai(1);
                record.setFasongjieguo("发送成功");
                record.setCuowuxinxi("");
            } else {
                int retryCount = notification.getChongshicishu() == null ? 0 : notification.getChongshicishu();
                if (retryCount >= 3) {
                    notification.setSongzhuangtai(3);
                    record.setFasongjieguo("发送失败（已达到最大重试次数）");
                } else {
                    notification.setChongshicishu(retryCount + 1);
                    notification.setSongzhuangtai(2);
                    record.setFasongjieguo("发送失败");
                }
                record.setCuowuxinxi(errorMessage.isEmpty() ? "通知发送失败，稍后将重试" : errorMessage);
            }

            notification.setZuihoufasongshijian(new Date());
            jiuzhentongzhiService.updateById(notification);
            tongzhijiluService.insert(record);

        } catch (Exception e) {
            e.printStackTrace();
            int retryCount = notification.getChongshicishu() == null ? 0 : notification.getChongshicishu();
            if (retryCount >= 3) {
                notification.setSongzhuangtai(3);
            } else {
                notification.setChongshicishu(retryCount + 1);
                notification.setSongzhuangtai(2);
            }
            notification.setZuihoufasongshijian(new Date());
            jiuzhentongzhiService.updateById(notification);

            TongzhijiluEntity record = new TongzhijiluEntity();
            record.setTongzhiid(notification.getId());
            record.setZhanghao(notification.getZhanghao());
            record.setShouji(notification.getShouji());
            record.setTongzhineirong(buildNotificationContent(notification));
            record.setFasongjieguo("发送异常");
            record.setCuowuxinxi(e.getMessage());
            record.setAddtime(new Date());
            tongzhijiluService.insert(record);
        }
    }

    private String buildNotificationContent(JiuzhentongzhiEntity notification) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String typeText = "";
        Integer type = notification.getTongzhileixing();
        if (type == null) type = 0;
        switch (type) {
            case 0:
                typeText = "预约成功提醒";
                break;
            case 1:
                typeText = "就诊前1天提醒";
                break;
            case 2:
                typeText = "就诊前1小时提醒";
                break;
        }
        String jiuzhenTime = notification.getJiuzhenshijian() != null ? 
            sdf.format(notification.getJiuzhenshijian()) : "未知";
        return String.format("[%s] 尊敬的用户，您预约的就诊时间为 %s，请按时就诊。", 
            typeText, jiuzhenTime);
    }

}

