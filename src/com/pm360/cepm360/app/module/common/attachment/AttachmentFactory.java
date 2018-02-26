package com.pm360.cepm360.app.module.common.attachment;



public class AttachmentFactory {
	
	@SuppressWarnings("rawtypes")
	public DialogInterface createAttachment(int type) {
		DialogInterface attachmentInterface = null;
		switch(type) {
		case 9:
			attachmentInterface = new TaskFeedbackAttachment(1);
			break;
		case 10:
			attachmentInterface = new TaskPictureAttachment(1);
			break;
		case 17:
			attachmentInterface = new TaskFeedbackAttachment(2);
			break;
		case 18:
			attachmentInterface = new TaskSafetyAttachment();
			break;
		case 19:
			attachmentInterface = new TaskQualityAttachment();
			break;
		case 20:
			attachmentInterface = new ZHTaskRiskAttachment();
			break;
		case 21:
			attachmentInterface = new TaskPictureAttachment(2);
			break;
		}
		return attachmentInterface;
	}


}
