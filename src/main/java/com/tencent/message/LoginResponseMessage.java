package com.tencent.message;

import lombok.Data;
import lombok.ToString;

/**
 * 登录请求回应信息
 */
@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage{
    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }

    public LoginResponseMessage(boolean success, String reason){
        super(success, reason);
    }
}
