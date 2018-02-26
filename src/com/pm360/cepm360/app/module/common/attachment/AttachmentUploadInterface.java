package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;



public interface AttachmentUploadInterface<T> {
	
	void updateParentBean(DataManagerInterface manager, T t);
}
