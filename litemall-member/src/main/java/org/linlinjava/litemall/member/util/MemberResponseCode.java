package org.linlinjava.litemall.member.util;

public class MemberResponseCode {
    public static final Integer AUTH_INVALID_ACCOUNT = 700;
    public static final Integer AUTH_CAPTCHA_UNSUPPORT = 701;
    public static final Integer AUTH_CAPTCHA_FREQUENCY = 702;
    public static final Integer AUTH_CAPTCHA_UNMATCH = 703;
    public static final Integer AUTH_NAME_REGISTERED = 704;
    public static final Integer AUTH_MOBILE_REGISTERED = 705;
    public static final Integer AUTH_MOBILE_UNREGISTERED = 706;
    public static final Integer AUTH_INVALID_MOBILE = 707;
    public static final Integer AUTH_OPENID_UNACCESS = 708;
    public static final Integer AUTH_OPENID_BINDED = 709;
    //用户已存在
    public static final Integer MEMBER_IS_EXIST = 800;
    public static final Integer CANNOT_BE_NEGATIVE = 801;
    public static final Integer USERID_NOT_PAYUID = 802;
    public static final Integer ORDER_IS_DELETED = 803;
    public static final Integer ORDER_IS_SUCCESS = 804;
}
