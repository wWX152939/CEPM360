package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.entity.AttachCell;

public class AttachmentReadOnlyActivity<T extends AttachCell> extends AttachmentActivity<T> {

    protected void initWindow() {
    	super.initWindow();
    	mDocumentUploadView.switchReadOnlyMode(true);
    }
}
