package com.pm360.cepm360.app.module.email;

import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮箱工具
 * @author yuanlu
 *
 */
public class MailBoxUtils {
	public static final boolean DBG = false;
	
	public static final String LINE_STRING = "-";
	public static final String COMMA_STRING = ",";
	public static final String LEFT_BRACKET_STRING = "[";
	public static final String RIGHT_BRACKET_STRING = "]";
	public static final String EMPTY_STRING = "";
	
	public static final String TO_STRING = "to";
	public static final String CC_STRING = "cc";
	
	public static final int KB_SIZE = 1024;
	
	/**
	 * 获取用户Id前导逗号分隔的用户Id字符串数组
	 * @param userLeaders
	 * @return
	 */
	public static List<String> getLeaderUserIds(String userLeaders) {
		if (DBG) {
			LogUtil.e("userLeaders = " + userLeaders);
		}
		
		List<String> userIds = new ArrayList<String>();
		
		if (userLeaders.equals(EMPTY_STRING)) {
			return userIds;
		}
		
		String[] users = userLeaders.split(",");
		
		for (int i = 0; i < users.length; i++) {
			userIds.add(users[i].substring(1, users[i].indexOf(LINE_STRING)));
		}
		
		return userIds;
 	}
	
	/**
	 * 获取用户Id前导逗号分隔的用户Id字符串
	 * @param userLeader
	 * @return
	 */
	public static String getLeaderUserId(String userLeader) {
			return userLeader.substring(1, userLeader.indexOf(LINE_STRING));
 	}
	
	/**
	 * receiver, receiver_type, is_read等
	 * 字段的是否包含指定用户Id的判断
	 * @param source
	 * @param userId
	 * @return
	 */
	public static boolean contain(String source, int userId) {
		String target = LEFT_BRACKET_STRING + userId + LINE_STRING;
		return source.contains(target);
	}
	
	/**
	 * 获取后导字符串数组
	 * @param userLeaders
	 * @return
	 */
	public static String[]	getBackGuarders(String userLeaders) {
		String[] users = userLeaders.split(",");
		String[] backGuarders = new String[users.length];
		
		for (int i = 0; i < users.length; i++) {
			backGuarders[i] = users[i].substring(
					users[i].indexOf(LINE_STRING) + 1, users.length - 1);
		}
		
		return backGuarders;
 	}
	
	/**
	 * 设置邮件度标识
	 * @param mailBox
	 * @param user
	 * @param isRead
	 */
	public static void setEmailRead(MailBox mailBox, User user, String isRead) {
		String[] readParts = mailBox.getIs_read().split(COMMA_STRING);
		
		String targetLeader = LEFT_BRACKET_STRING + user.getUser_id() + LINE_STRING;
		for (int i = 0; i < readParts.length; i++) {
			String readPart = readParts[i];
			if (readPart.contains(targetLeader)) {
				readParts[i] = targetLeader + isRead + RIGHT_BRACKET_STRING;
			}
		}
		
		mailBox.setIs_read(catWithComma(readParts));
	}
	
	/**
	 * 逗号分隔拼接字符串数组
	 * @param source
	 * @return
	 */
	private static String catWithComma(String[] source) {
		StringBuffer catBuffer = new StringBuffer();
		
		for (String part : source) {
			if (part != null) {
				catBuffer.append(part);
				catBuffer.append(COMMA_STRING);
			}
		}
		
		return catBuffer.subSequence(0, catBuffer.length() - 1).toString();
	}
	
	/**
	 * 邮件的大小，包括附件的大小，最小为1KB
	 * @param mailBox
	 * @param fileList
	 * @return
	 */
	public static long getEmailSize(MailBox mailBox, List<Files> fileList) {
		long totleSize = mailBox.getContent().length();
		
		if (fileList != null) {
			for (Files file : fileList) {
				totleSize += file.getFile_size();
			}
		}
		
		if (totleSize < KB_SIZE) {
			totleSize = KB_SIZE;
		}
		
		return totleSize;
	}
	
	/**
	 * 获取接受者或抄送者列表
	 * @param mailBox
	 * @param isCc
	 * @return
	 */
	public static List<String> getRecieverIds(MailBox mailBox, boolean isCc) {
		return getRecieverIds(mailBox.getReceiver(), mailBox.getReceive_type(), isCc);
	}
	
	/**
	 * 获取接受者或抄送者列表
	 * @param mailBox
	 * @param isCc
	 * @return
	 */
	public static List<String> getRecieverIds(String recievers,
								String recieverType, boolean isCc) {
		List<String> recieverIds = getLeaderUserIds(recievers);
		List<String> recieverList = new ArrayList<String>();
		
		if (recieverIds.isEmpty()) {
			return recieverList;
		}
		
		String[] typeParts = recieverType.split(COMMA_STRING);
		for (String type : typeParts) {
			if (!isCc) {
				if (type.contains(TO_STRING)) {
					for (String reciever : recieverIds) {
						if (reciever.equals(getLeaderUserId(type))
								&& !recieverList.contains(reciever)) {
							recieverList.add(reciever);
						}
					}
				}
			} else {
				if (type.contains(CC_STRING)) {
					for (String reciever : recieverIds) {
						if (reciever.equals(getLeaderUserId(type))
								&& !recieverList.contains(reciever)) {
							recieverList.add(reciever);
						}
					}
				}
			}
		}
		
		return recieverList;
	}
	
	/**
	 * 判断指定用户是否为接受者（包括抄送人）
	 * @param mailBox
	 * @param user
	 * @return
	 */
	public static boolean isReceiver(MailBox mailBox, User user) {
		String receiver = mailBox.getReceiver();
		if (receiver.contains(LEFT_BRACKET_STRING
					+ user.getUser_id() + LINE_STRING)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 某人的某邮件是否已读
	 * @param mailBox
	 * @param user
	 * @return
	 */
	public static boolean isMailReaded(MailBox mailBox, User user) {
		String read = mailBox.getIs_read();
		String leader = LEFT_BRACKET_STRING + user.getUser_id() + LINE_STRING;
		
		if (read.contains(leader)) {
			String readFlag = getBackGuarder(read, leader);
			if (readFlag != null && readFlag.equals(GLOBAL.MSG_READ[0][0])) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 构建读标识字段
	 * @param to
	 * @param cc
	 * @return
	 */
	public static String buildRead(List<User> to, List<User> cc) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(buildRead(to));
		
		// 如果toList不为空，需要和ccList内容逗号分隔
		if (!sb.toString().equals(EMPTY_STRING) && !cc.isEmpty()) {
			sb.append(COMMA_STRING);
			sb.append(buildRead(cc));
		}
		
		return sb.toString();
	}
	
	/**
	 * 构建读标识字段
	 * @param users
	 * @return
	 */
	public static String buildRead(List<User> users) {
		StringBuffer sb = new StringBuffer("");
		
		if (!users.isEmpty()) {
			for (User user : users) {
				sb.append(LEFT_BRACKET_STRING + user.getUser_id());
				sb.append(LINE_STRING);
				sb.append(GLOBAL.MSG_READ[0][0] + RIGHT_BRACKET_STRING);
				sb.append(COMMA_STRING);
			}
		} else {
			return sb.toString();
		}
		
		return sb.subSequence(0, sb.length() - 1).toString();
	}
	
	/**
	 * 构建读标识字段
	 * @param user
	 * @return
	 */
	public static String buildRead(User user) {
		StringBuffer sb = new StringBuffer("");
		
		if (user != null) {
			sb.append(LEFT_BRACKET_STRING + user.getUser_id());
			sb.append(LINE_STRING);
			sb.append(GLOBAL.MSG_READ[0][0] + RIGHT_BRACKET_STRING);
			sb.append(COMMA_STRING);
		} else {
			return sb.toString();
		}
		
		return sb.subSequence(0, sb.length() - 1).toString();
	}
	
	/**
	 * 读取指定前导字符串后的字符串
	 * @param source
	 * @param leader
	 * @return
	 */
	private static String getBackGuarder(String source, String leader) {
		String[] parts = source.split(COMMA_STRING);
		for (String part : parts) {
			if (part.contains(leader)) {
				return part.substring(leader.length(), part.length() - 1);
			}
		}
		return null;
	}
	
	/**
	 * 删除指定前导字符串的项
	 * @param source
	 * @return
	 */
	public static String deleteLeaderPart(String source) {
		String[] parts = source.split(COMMA_STRING);
		String leader = LEFT_BRACKET_STRING 
				+ UserCache.getCurrentUser().getUser_id() + LINE_STRING;
		
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(leader)) {
				StringBuffer temp = new StringBuffer();
				
				temp.append(parts[i].substring(0, parts[i].length() - 2));
				temp.append(GLOBAL.MSG_DEL[1][0]);
				temp.append(RIGHT_BRACKET_STRING);
				
				parts[i] = temp.toString();
			}
		}
		
		return catWithComma(parts);
	}
	
	/**
	 * 删除指定前导字符串的项，被接受者删除使用
	 * @param source
	 * @return
	 */
	public static String deletePart(String source, boolean isCc) {
		String[] parts = source.split(COMMA_STRING);
		String leader = LEFT_BRACKET_STRING 
				+ UserCache.getCurrentUser().getUser_id() + LINE_STRING;
		String target = null;
		if (isCc) {
			target = leader + CC_STRING;
		} else {
			target = leader + TO_STRING;
		}
		
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(target)) {
				parts[i] = null;
			}
		}
		
		return catWithComma(parts);
	}
	
	/**
	 * 拼接用户租户字符串
	 * @param users
	 * @return
	 */
	public static String buildReceiver(List<User> to, List<User> cc) {
		StringBuffer receiver = new StringBuffer();
		
		receiver.append(buildReceiver(to));
		
		// 如果toList不为空，需要和ccList内容逗号分隔
		if (!receiver.toString().equals(EMPTY_STRING)
				&& !cc.isEmpty()) {
			receiver.append(COMMA_STRING);
			receiver.append(buildReceiver(cc));
		}
		
		return receiver.toString();
	}
	
	/**
	 * 拼接用户租户字符串
	 * @param users
	 * @return
	 */
	public static String buildReceiver(List<User> users) {
		StringBuffer receiver = new StringBuffer();
		
		if (users != null && !users.isEmpty()) {
			for (User user : users) {
				if (user != null) {
					receiver.append(LEFT_BRACKET_STRING + user.getUser_id());
					receiver.append(LINE_STRING);
					receiver.append(user.getTenant_id() + RIGHT_BRACKET_STRING);
					receiver.append(COMMA_STRING);
				}
			}
		} else {
			return receiver.toString();
		}
		
		return receiver.subSequence(0, receiver.length() - 1).toString();
	}
	
	/**
	 * 拼接附件Id
	 * @param fileList
	 * @return
	 */
	public static String buildAttachmentIds(List<Files> fileList) {
		StringBuffer receiver = new StringBuffer("");
		
		if (fileList == null || fileList.isEmpty()) {
			return receiver.toString();
		}
		
		for (Files files : fileList) {
			if (files != null) {
				receiver.append(files.getFile_id());
				receiver.append(COMMA_STRING);
			}
		}
		
		return receiver.subSequence(0, receiver.length() - 1).toString();
	}
	
	/**
	 * 拼接接收者类型字符串
	 * @param toList
	 * @param ccList
	 * @return
	 */
	public static String buildReceiverType(List<User> toList, List<User> ccList) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(buildReceiverType(toList, false));
		
		// 如果toList不为空，需要和ccList内容逗号分隔
		if (!sb.toString().equals(EMPTY_STRING)
				&& !ccList.isEmpty()) {
			sb.append(COMMA_STRING);
			sb.append(buildReceiverType(ccList, true));
		}
		
		return sb.toString();
	}
	
	/**
	 * 拼接接收者类型字符串
	 * @param user
	 * @return
	 */
	public static String buildReceiverType(List<User> userList, boolean isCc) {
		StringBuffer sb = new StringBuffer();
		
		if (userList != null && !userList.isEmpty()) {
			for (User user : userList) {
				if (user != null) {
					sb.append(LEFT_BRACKET_STRING + user.getUser_id());
					sb.append(LINE_STRING + (isCc ? CC_STRING : TO_STRING));
					sb.append(RIGHT_BRACKET_STRING + COMMA_STRING);
				}
			}
		} else {
			return sb.toString();
		}
		
		return sb.subSequence(0, sb.length() -1).toString();
	}
	
	/**
	 * 拼接删除字符串
	 * @param toList
	 * @param ccList
	 * @return
	 */
	public static String buildDelIn(List<User> toList, List<User> ccList) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(buildDelIn(toList));
		
		// 如果toList不为空，需要和ccList内容逗号分隔
		if (!sb.toString().equals(EMPTY_STRING)
				&& !ccList.isEmpty()) {
			sb.append(COMMA_STRING);
			sb.append(buildDelIn(ccList));
		}
		
		return sb.toString();
	}
	
	/**
	 * 拼接删除字符串
	 * @param toList
	 * @param ccList
	 * @return
	 */
	public static String buildDelIn(List<User> users) {
		StringBuffer sb = new StringBuffer(EMPTY_STRING);
		
		if (users != null && !users.isEmpty()) {
			for (User to : users) {
				if (to != null) {
					sb.append(LEFT_BRACKET_STRING + to.getUser_id());
					sb.append(LINE_STRING + GLOBAL.MSG_DEL[0][0]);
					sb.append(RIGHT_BRACKET_STRING + COMMA_STRING);
				}
			}
		} else {
			return sb.toString();
		}
		
		return sb.subSequence(0, sb.length() -1).toString();
	}
	
	/**
	 * 生成邮箱列表中所有联系人ID的字符串
	 * @param mailBoxs 邮箱列表
	 * @return
	 */
	public static String getAllContacts(List<MailBox> mailBoxs) {
		StringBuilder sb = new StringBuilder();
		
		// 添加所有联系人ID，不重复添加
		List<String> contactIds = new ArrayList<String>();
		for (int i = 0; i < mailBoxs.size(); i++) {
			MailBox mailBox = mailBoxs.get(i);
			
			// 添加发件人
			if (!contactIds.contains(mailBox.getSender() + "")) {
				contactIds.add(mailBox.getSender() + "");
			}
			
			List<String> receiverIds = getRecieverIds(mailBox, true);
			receiverIds.addAll(getRecieverIds(mailBox, false));
	
			// 添加收件人
			for (int j = 0; j < receiverIds.size(); j++) {
				if (!contactIds.contains(receiverIds.get(j))) {
					contactIds.add(receiverIds.get(j));
				}
			}
		}
		
		// 拼接联系人ID字符串
		for (int i = 0; i < contactIds.size(); i++) {
			sb.append(contactIds.get(i) + ",");
		}
		
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
	}
 }
