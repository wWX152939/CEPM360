package com.pm360.cepm360.app.module.common.attachment;

import android.content.Intent;

import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.Expandable;

public abstract class AttachmentReadOnlyFragment<T extends AttachCell, B extends Expandable>
		extends AttachmentFragment<T, B> {

	@Override
	protected Intent setAttachmentActivity() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), AttachmentReadOnlyActivity.class);
		return intent;
	}


}
