package org.linlinjava.litemall.member.service;


import org.linlinjava.litemall.core.util.BeanUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.LitemallMemberPriceMapper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallOrderService;

import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


/**
 * @author zhy
 * @date 2019-01-17 23:07
 **/
@Component
public class MemberService {
	@Resource
	private LitemallUserMapper litemallUserMapper;

	@Resource
	private LitemallMemberPriceMapper litemallMemberPriceMapper;

	@Resource
	private LitemallOrderMapper litemallOrderMapper;

	@Autowired
	private LitemallOrderService orderService;

	public Object getPrice(){
		LitemallMemberPriceExample litemallMemberPriceExample = new LitemallMemberPriceExample();
		List<LitemallMemberPrice> litemallMemberPrices = litemallMemberPriceMapper.selectByExample(litemallMemberPriceExample);
		return ResponseUtil.ok(litemallMemberPrices);
	}

	public Object getOrder(Integer userId, Integer showType, Integer page, Integer limit, String sort, String order){
		//根据订单类型查询是否是会员订单
		List<Short> orderStatus = OrderUtil.orderStatus(showType);
		List<LitemallOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus, page, limit, sort, order);
		return ResponseUtil.okList(orderList, orderList);
	}

	public Object purchaseMember(String userId, BigDecimal amount , String level) {

		//创建订单
		short a = 6;
		LitemallOrder litemallOrder = new LitemallOrder();
		litemallOrder.setOrderPrice(amount);
		litemallOrder.setAddTime(LocalDateTime.now());
		litemallOrder.setUserId(Integer.parseInt(userId));
		litemallOrder.setOrderStatus(a);
		litemallOrderMapper.insert(litemallOrder);

		return ResponseUtil.ok();
	}


	public Object memberRenewal(String userId) {
		LitemallUserExample litemallUserExample = new LitemallUserExample();
		//查询用户对象
		LitemallUser litemallUser = litemallUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		//获取用户会员到期时间和当前时间
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime memberDatetime = litemallUser.getMemberDatetime();
		//判断用户是否还是会员
		int i = now.compareTo(memberDatetime);
		System.out.println(i);
		//如果会员还没有到期,那就从未到期时间开始相加,反之从现在
		if (1 == i) {
			litemallUser.setMemberDatetime(memberDatetime);
			litemallUser.setIsMember(true);
			litemallUserMapper.updateByExampleSelective(litemallUser,litemallUserExample);
		}
		litemallUser.setMemberDatetime(now);
		litemallUser.setIsMember(true);
		litemallUserMapper.updateByExampleSelective(litemallUser,litemallUserExample);
		return ResponseUtil.ok();
	}

	public Object memberStatus(Integer userId) {
		LitemallUserExample litemallUserExample = new LitemallUserExample();
		litemallUserExample.createCriteria().andIdEqualTo(userId).andIsMemberEqualTo(true).andMemberDatetimeLessThan(LocalDateTime.now());
		LitemallUser litemallUser = litemallUserMapper.selectOneByExample(litemallUserExample);
		if (null != litemallUser){
			return ResponseUtil.ok();
		}
		return ResponseUtil.fail();
	}
}
