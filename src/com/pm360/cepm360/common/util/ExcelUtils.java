package com.pm360.cepm360.common.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtils {
	
	/**
	 * 
	 * @param fileName path and fileName
	 * @param mapList
	 * @param title
	 * @return
	 */
	public static boolean writeExcel(String path, String fileName, List<Map<String, String>> mapList,  String[] title) {
		WritableWorkbook wwb = null;
		try {
			// 创建一个可写入的工作薄(Workbook)对象
			isExist(path,fileName);
			wwb = Workbook.createWorkbook(new File(path+fileName));
			Log.v("ExcelUtils","create success");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (wwb != null) {
			// 第一个参数是工作表的名称，第二个是工作表在工作薄中的位置
			WritableSheet ws = wwb.createSheet("sheet1", 0);
			 
			if (title == null) return false;
			// 在指定单元格插入数据
			for (int i = 0; i < title.length; i++) {
				
				WritableFont fontTitle= new WritableFont(WritableFont.TIMES,13,WritableFont.BOLD);
				WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);
				
				try {
					fontTitle.setColour(Colour.WHITE);
					formatTitle.setBackground(Colour.DARK_BLUE2);
					formatTitle.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);				
					formatTitle.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
					formatTitle.setAlignment(Alignment.CENTRE);
					formatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
				} catch (WriteException e) {
					e.printStackTrace();
				}
				Label lbl1 = new Label(i, 0, title[i],formatTitle);
				try {
					ws.setRowView(0, 400);
					ws.addCell(lbl1);
				} catch (RowsExceededException e1) {
					e1.printStackTrace();
				} catch (WriteException e1) {
					e1.printStackTrace();
				}
				
				for (int ii = 0; ii < mapList.size(); ii++){
					WritableFont fontContent= new WritableFont(WritableFont.TIMES,12,WritableFont.NO_BOLD);
					WritableCellFormat formatContent = new WritableCellFormat(fontContent);			
					try {
						formatContent.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
						formatContent.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
						if (i != 0) {
							formatContent.setAlignment(Alignment.CENTRE);
						}
						formatContent.setVerticalAlignment(VerticalAlignment.CENTRE);
						ws.setRowView(ii+1, 350);
					} catch (WriteException e) {
						e.printStackTrace();
					}					
					Label lbl2 = new Label(i, ii+1, mapList.get(ii).get(title[i]), formatContent);
					try {
						ws.addCell(lbl2);
					} catch (RowsExceededException e1) {
						e1.printStackTrace();
					} catch (WriteException e1) {
						e1.printStackTrace();
					}		
					
				}	
				
				ws.setColumnView(i, 15);//根据内容自动设置列宽
				
			}	

			try {
				// 从内存中写入文件中
				wwb.write();
				wwb.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	public static void isExist(String path, String fileName) {
		File dir = new File(path);
		//判断文件夹是否存在,如果不存在则创建文件夹
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File(path+fileName);
		if (file.exists()) {
			file.delete();
			Log.v("ccc","file??"+file.exists());
		}
	}
	
	public class ExcelBestColumn
	{
	    
	    public void writeDataToSheet(WritableSheet sheet,List<List<String>> list) throws Exception{
	        int columnBestWidth[]=new  int[list.get(0).size()];    //保存最佳列宽数据的数组
	        
	        for(int i=0;i<list.size();i++){
	            List<String> row=list.get(i);
	            for(int j=0;j<row.size();j++){
	                 sheet.addCell(new Label(j,i,row.get(j)));
	                 
	                 int width=row.get(j).length()+getChineseNum(row.get(j));    ///汉字占2个单位长度
	                 if(columnBestWidth[j]<width)    ///求取到目前为止的最佳列宽
	                     columnBestWidth[j]=width;
	            }
	        }
	        
	        for(int i=0;i<columnBestWidth.length;i++){    ///设置每列宽
	            sheet.setColumnView(i, columnBestWidth[i]);
	        }
	    }
	    
	    public int getChineseNum(String context){    ///统计context中是汉字的个数
	        int lenOfChinese=0;
	        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");    //汉字的Unicode编码范围
	        Matcher m = p.matcher(context);
	        while(m.find()){
	            lenOfChinese++;
	        }
	        return lenOfChinese;
	    }
	}	
	
	public static String readExcel(String path, int x, int y) {
		String content = "";
		try {
			Workbook book = Workbook.getWorkbook(new File(path));
			
			if (book != null) {
				Sheet sheet = book.getSheet(0);
				// 得到x行y列所在单元格的内容
				String cellStr = sheet.getRow(x)[y].getContents();
				content = cellStr;
			}
		} catch (BiffException e) {
			content = "";
			e.printStackTrace();
		} catch (IOException e) {
			content = "";
			e.printStackTrace();
		}
		return content;

	}
}
