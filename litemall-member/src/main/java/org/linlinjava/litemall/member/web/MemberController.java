package org.linlinjava.litemall.member.web;


import io.swagger.models.auth.In;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.member.service.MemberService;
import org.linlinjava.litemall.member.vo.LitemallOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;


/**
 * 会员基本操作
 */
@RestController
@RequestMapping("/member")
@Validated
public class MemberController {
	@Autowired
	MemberService memberService;

	/**
	 * 会员价格查询
	 * @return 价格
	 */
	@PostMapping("/price")
	public Object getPrice() {
        return memberService.getPrice();
	}


	/**
	 * 会员订单查询
	 *param userId ;
	 * @return 用户订单
	 */
	@PostMapping("/order")
	public Object getOrder(@RequestParam Integer userId,
						   @RequestParam(defaultValue = "5") Integer showType,//显示订单类型/假定会员订单状态为6
						   @RequestParam(defaultValue = "1") Integer page,
						   @RequestParam(defaultValue = "10") Integer limit,
						   @Sort @RequestParam(defaultValue = "add_time") String sort,
						   @Order @RequestParam(defaultValue = "desc") String order) {
		return memberService.getOrder(userId, showType, page, limit, sort, order);
	}

	/**
	 * 会员购买
	 * @param userId ,amount ,time ;
	 * @return 价格 , 金额  ，时间
	 */
	@PostMapping("/purchaseMember")
	public Object purchaseMember(@RequestParam String userId, @RequestParam BigDecimal amount, @RequestParam Integer time, HttpServletRequest req) {
		return memberService.purchaseMember(userId,amount);
	}

	/**
	 * 会员续费
	 * @param userId ,amount ,time ;
	 * @return 价格 , 金额  ，时间
	 */
	@PostMapping("/memberRenewal")
	public Object memberRenewal(@RequestParam String userId,@RequestParam BigDecimal amount, @RequestParam Integer time,HttpServletRequest req) {
		return memberService.memberRenewal(userId,amount);
	}

	/**
	 * 会员状态
	 * @param userId ;
	 * @return 价格
	 */
	@PostMapping("/memberStatus")
	public Object memberStatus(@RequestParam String userId) {
		return memberService.memberStatus(Integer.parseInt(userId));
	}

	/**
	 * 微信支付
	 * @param userId;
	 * @return 价格
	 */
	@PostMapping("/weChatPay")
	public Object weChatPay(@RequestParam String userId, @RequestParam Integer time,@RequestBody LitemallOrderVo litemallOrderVo,HttpServletRequest req) {
		return memberService.weChatPay(userId,time,litemallOrderVo,req);
	}
}