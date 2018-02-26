//package com.pm360.cepm360.app.common.ticket;
//
//import com.pm360.cepm360.entity.P_CGJH;
//import com.pm360.cepm360.entity.P_CGYS;
//
//public class TicketFactory<T> {
//	public T createTicket() {
//		T a = null;
//		switch(getFlowType()) {
//		//GLOBAL.FLOW_TYPE
//		case "4":
//			a = (T) new P_CGJH();
//			mListItemClass = (Class<T>) P_CGJH.class;
//			break;
//		case "5":
//			a = (T) new P_CGYS();
//			mListItemClass = (Class<T>) P_CGYS.class;
//		}
//		return a;
//		
//	}
//}
