package com.pm360.cepm360.app.common.custinterface;

import java.util.List;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;

public interface SimpleTicketServiceInterface<A, B> {
	
	void getDetailServerData(final DataManagerInterface manager,
			int typeId);
	
	void addServerData(final DataManagerInterface manager,
			A topData, List<B> detailData);
	
	void updateServerData(final DataManagerInterface manager,
			A topData, List<B> detailData);
	
}
