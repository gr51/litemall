package org.linlinjava.litemall.member.service;


import io.swagger.models.auth.In;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.LitemallMemberPriceMapper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.LitemallOrderService;

import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.member.util.CopyPropertiesUtil;
import org.linlinjava.litemall.member.util.MemberResponseCode;
import org.linlinjava.litemall.member.vo.LitemallOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

	@Transactional(propagation = Propagation.SUPPORTS)
	public Object getPrice(){
		LitemallMemberPriceExample litemallMemberPriceExample = new LitemallMemberPriceExample();
		List<LitemallMemberPrice> litemallMemberPrices = litemallMemberPriceMapper.selectByExample(litemallMemberPriceExample);
		return ResponseUtil.ok(litemallMemberPrices);
	}


	@Transactional(propagation = Propagation.SUPPORTS)
	public Object getOrder(Integer userId, Integer orderType, Integer page, Integer limit, String sort, String order){
		//根据订单类型查询是否是会员订单
		List<Short> orderStatus = OrderUtil.orderStatus(orderType);
		List<LitemallOrder> orderList = orderService.queryByOrderType(userId, orderStatus, page, limit, sort, order);
		return ResponseUtil.okList(orderList, orderList);
	}
	@Transactional
	public Object purchaseMember(String userId, BigDecimal amount ) {
		int userId1 = Integer.parseInt(userId);
		LitemallUser litemallUser = litemallUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (null == litemallUser.getIsMember()){
			litemallUser.setIsMember(false);
		}
		if (!litemallUser.getIsMember()) {
			//创建订单
			LitemallOrder litemallOrder = new LitemallOrder();
			litemallOrder.setOrderPrice(amount);
			//litemallOrder.setOrderSn(MemberService.class.getName() + userId + new Date());
			litemallOrder.setActualPrice(amount);
			litemallOrder.setGoodsPrice(amount);
			litemallOrder.setOrderSn("20150806125346");
			litemallOrder.setAddTime(LocalDateTime.now());
			litemallOrder.setUserId(userId1);
			litemallOrder.setOrderStatus(OrderUtil.STATUS_CREATE);
			litemallOrder.setDeleted(false);
			litemallOrder.setOrderType(OrderUtil.PURCHASE_MEMBER_TYPE);
			litemallOrder.setConsignee(litemallUser.getUsername());
			litemallOrder.setMobile(litemallUser.getMobile());
			litemallOrderMapper.insert(litemallOrder);
			LitemallOrderVo litemallOrderVo = CopyPropertiesUtil.copySourceObjToTargetObj(litemallOrder, LitemallOrderVo.class);
			assert litemallOrderVo != null;
			litemallOrderVo.setOrderId(litemallOrder.getId());
			return ResponseUtil.ok(litemallOrderVo);
		}
		return ResponseUtil.fail(MemberResponseCode.MEMBER_IS_EXIST,"已经是会员");
	}

	@Transactional
	public Object memberRenewal(String userId ,BigDecimal amount) {
		LitemallUser litemallUser = litemallUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		int userId1 = Integer.parseInt(userId);
		//创建订单
		LitemallOrder litemallOrder = new LitemallOrder();
		litemallOrder.setOrderPrice(amount);
		//litemallOrder.setOrderSn(MemberService.class.getName() + userId + new Date());
		litemallOrder.setOrderSn("20150806125346");
		litemallOrder.setActualPrice(amount);
		litemallOrder.setGoodsPrice(amount);
		litemallOrder.setConsignee(litemallUser.getUsername());
		litemallOrder.setMobile(litemallUser.getMobile());
		litemallOrder.setAddTime(LocalDateTime.now());
		litemallOrder.setUserId(userId1);
		litemallOrder.setOrderStatus(OrderUtil.STATUS_CREATE);
		litemallOrder.setDeleted(false);
		litemallOrder.setOrderType(OrderUtil.MEMBER_RENEWAL_TYPE);
		litemallOrderMapper.insert(litemallOrder);
		LitemallOrderVo litemallOrderVo = CopyPropertiesUtil.copySourceObjToTargetObj(litemallOrder, LitemallOrderVo.class);
		assert litemallOrderVo != null;
		litemallOrderVo.setOrderId(litemallOrder.getId());

		return ResponseUtil.ok(litemallOrderVo);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public Object memberStatus(Integer userId) {
		LitemallUserExample litemallUserExample = new LitemallUserExample();
		litemallUserExample.createCriteria().andIdEqualTo(userId).andIsMemberEqualTo(true).andMemberDatetimeLessThan(LocalDateTime.now());
		LitemallUser litemallUser = litemallUserMapper.selectOneByExample(litemallUserExample);
		if (null == litemallUser){
			return ResponseUtil.fail(MemberResponseCode.MEMBER_NOT_FOUND,"非法用户");
		}
		return ResponseUtil.ok(litemallUser.getIsMember());
	}
	@Transactional
	public Object weChatPay( String userId, Integer time, LitemallOrderVo litemallOrderVo,HttpServletRequest req){
		if (!litemallOrderVo.getUserId().equals(Integer.parseInt(userId))){
			return ResponseUtil.fail(MemberResponseCode.USERID_NOT_PAYUID,"当前用户不是支付人");
		}
		LitemallUser litemallUser = litemallUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (null == litemallUser){
			return ResponseUtil.fail(MemberResponseCode.MEMBER_NOT_FOUND,"非法用户");
		}
		//判断订单状态
		LitemallOrder litemallOrder = litemallOrderMapper.selectByPrimaryKey(litemallOrderVo.getOrderId());
		if (litemallOrder.getDeleted()){
			return ResponseUtil.fail(MemberResponseCode.ORDER_IS_DELETED,"此订单已被删除");
		}else {
			if ( OrderUtil.STATUS_CONFIRM.equals((Short)litemallOrder.getOrderStatus())){
				return ResponseUtil.fail(MemberResponseCode.ORDER_IS_SUCCESS,"此订单已被支付");
			}
		}
		LitemallOrderExample litemallOrderExample = new LitemallOrderExample();
		litemallOrderExample.createCriteria().andDeletedEqualTo(false).andIdEqualTo(litemallOrder.getId());
		LitemallUserExample litemallUserExample = new LitemallUserExample();
		litemallUserExample.createCriteria().andDeletedEqualTo(false).andIdEqualTo(litemallUser.getId());

		//获取用户会员到期时间和当前时间
		LocalDateTime memberDatetime = litemallUser.getMemberDatetime();
		LocalDateTime now = LocalDateTime.now();
		//todo 获取到支付成功后 判断是否成功
/*		try {
			Object o = wxOrderService.h5pay(Integer.parseInt(userId), JacksonUtil.toJson(litemallOrderVo),req);
		} catch (Exception e) {
			return ResponseUtil.fail(999, e.toString());
		}*/
		if (litemallOrderVo.getOrderType() == 1) {
			//支付成功之后,更新会员状态
			//设置到期时间

			LocalDateTime endTime = now.plusMonths(time);
			litemallUser.setMemberDatetime(endTime);
			litemallUser.setIsMember(true);
			litemallUserMapper.updateByExampleSelective(litemallUser, litemallUserExample);
			//修改支付成功的状态
			litemallOrder.setOrderStatus(OrderUtil.STATUS_CONFIRM);
			litemallOrderMapper.updateByExampleSelective(litemallOrder,litemallOrderExample);
		} else if (litemallOrderVo.getOrderType() == 2) {
			//判断用户是否还是会员
			boolean before = memberDatetime.isBefore(now);

			//如果会员还没有到期,那就从未到期时间开始相加,反之从现在
			if (!before) {
				//时间按月份相加
				litemallUser.setMemberDatetime(memberDatetime.plusMonths(time));
				litemallUser.setIsMember(true);
				litemallUserMapper.updateByExampleSelective(litemallUser, litemallUserExample);
				//修改支付成功的状态
				litemallOrder.setOrderStatus(OrderUtil.STATUS_CONFIRM);
				litemallOrderMapper.updateByExample(litemallOrder,litemallOrderExample);
			} else {
				litemallUser.setMemberDatetime(now.plusMonths(time));
				litemallUser.setIsMember(true);
				litemallUserMapper.updateByExampleSelective(litemallUser, litemallUserExample);
				//修改支付成功的状态
				litemallOrder.setOrderStatus(OrderUtil.STATUS_CONFIRM);
				litemallOrderMapper.updateByExampleSelective(litemallOrder,litemallOrderExample);
			}
		} else {
				return ResponseUtil.badArgumentValue();
		}
		return ResponseUtil.ok();
	}
}
