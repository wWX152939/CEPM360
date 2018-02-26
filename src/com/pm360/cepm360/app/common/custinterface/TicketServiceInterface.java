package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.entity.Flow_approval;

import java.util.List;

public interface TicketServiceInterface<A, B> extends SimpleTicketServiceInterface<A, B> {
	void getDataByMessageId(final DataManagerInterface manager,
			int messageId);
	
	void rebutApproval(final DataManagerInterface manager,
			A topData, Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approval);
	
	void passApproval(final DataManagerInterface manager,
			A topData, List<B> detailData,
			Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approval);
	
	void submitForAdd(final DataManagerInterface manager,
			A topData, List<B> detailData,
			Flow_approval Flow_approval, String flow_status);
	
	void submitForUpdate(final DataManagerInterface manager,
			A topData, List<B> detailData,
			Flow_approval Flow_approval, String flow_status);
}
