package com.pm360.cepm360.common.net;

import android.util.Log;

import com.pm360.cepm360.common.util.FromFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class FileSocketUpload {
	private static final String TAG = "HttpUploader";
	private static final boolean DEBUG = false;
	
	/* 鐏忓棙鏆熼幑顔藉絹娴溿倕鍩岄張宥呭閸ｎ煉绱濇稉锟介懜顑跨瑐娴肩姳璐熺悰銊ュ礋閺佺増宓侀崪灞炬瀮娴狅拷 */
	public static boolean post(String path, Map<String, String> params,
            FromFile[] files) throws Exception {
        final String BOUNDARY = "-----------------------7da2137580612";
        final String endLine = BOUNDARY + "--\r\n";

        if(DEBUG) Log.d(TAG, "Enter post");
        
        /* 鐠侊紕鐣婚幍锟介張澶嬫瀮娴犲墎娈戦梹鍨閿涘苯鎮楅棃顣檕cket鐠佸墽鐤嗛悽銊ュ煂  */
        int fileDataLength = 0;
        for (FromFile uploadFile : files) {
            StringBuilder fileExplain = new StringBuilder();
            
            fileExplain.append("--").append(BOUNDARY).append("\r\n");
            fileExplain.append("Content-Disposition:form-data;name=\"" + uploadFile.getParameterName() + 
            					"\";filename=" + uploadFile.getFileName() + "\"\r\n");
            
            fileExplain.append("Content-Type:" + uploadFile.getContentType() + "\r\n\r\n");
            fileExplain.append("\r\n");
            fileDataLength += fileExplain.length();
            
            if (uploadFile.getInputStream() != null) {
                fileDataLength += uploadFile.getFile().length();
            } else {
                fileDataLength += uploadFile.getData().length;
            }
        }
        
        /* 鐠侊紕鐣籶arams娴肩娀锟芥帞娈�(鐞涖劌宕熼弫鐗堝祦)閺傚洦婀伴弫鐗堝祦閻ㄥ嫭锟借銇囩亸锟� */
        StringBuilder textEntity = new StringBuilder();
        if (params != null && !params.isEmpty()) {
	        for(Map.Entry<String, String> entry : params.entrySet()) {
	        	textEntity.append("--");
	        	textEntity.append(BOUNDARY);
	        	textEntity.append("\r\n");
	        	textEntity.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
	        	textEntity.append(entry.getValue());
	        	textEntity.append("\r\n");
	        }
        }
        
        int dataLength = textEntity.toString().getBytes().length + fileDataLength + endLine.getBytes().length;
        
        URL url = new URL(path);
        int port = (url.getPort() == -1) ? 80 : url.getPort();
        
        /* 閸掓稑缂撴稉锟芥稉顏堬拷姘愁唵socket閿涘苯鑻熼懢宄板絿閸忔儼绶崙鐑樼ウ */
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
        OutputStream os = socket.getOutputStream();

        /* 闁俺绻冩潏鎾冲毉濞翠線鍘ょ純鐣噊cket閻ㄥ嫬褰傞柅浣哥潣閹嶇礉妫ｆ牕鍘涚�瑰本鍨歨ttp婢跺娈戦崣鎴︼拷锟� */
        String requestMethod = "POST" + url.getPath() + "HTTP/1.1\r\n";
        os.write(requestMethod.getBytes());
        
        String accept = "Accept:image/gif,image/jpeg,image/pjpeg," + 
        				"application/x-shockwave-flash,application/xaml+xml,application/vnd.ms-xpdocument,application/x-ms-xbap," + 
        				"application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*\r\n";
        os.write(accept.getBytes());
        
        String language = "Accept-Language: zh-CN\r\n";
        os.write(language.getBytes());
        
        String contenttype = "Content-Type: multipart/form-data; boundary="+ BOUNDARY+ "\r\n";
        os.write(contenttype.getBytes());
        
        //String contentlength = "Content-Length: "+ fileDataLength + "\r\n";
        String contentlength = "Content-Length: "+ dataLength + "\r\n";
        os.write(contentlength.getBytes());
        
        String alive = "Connection: Keep-Alive\r\n";
        os.write(alive.getBytes());
        
        String host = "Host: "+ url.getHost() +":"+ port +"\r\n";
        os.write(host.getBytes());   
        
        /* 閸愭瑥鐣琀TTP鐠囬攱鐪版径鏉戞倵閿涘本鐗撮幑鐡綯TP閸楀繗顔呴崘宥呭晸娑擄拷娑擃亜娲栨潪锔藉床鐞涳拷 */
        os.write("\r\n".getBytes());
        
        /* 閹跺﹥澧嶉張澶嬫瀮閺堫剛琚崹瀣畱鐞涖劌宕熺�圭偘缍嬮弫鐗堝祦閸欐垿锟戒礁鍤弶锟� */
        os.write(textEntity.toString().getBytes());
        
        /* 閹跺﹥澧嶉張澶嬫瀮娴犲墎琚崹瀣畱鐎圭偘缍嬮弫鐗堝祦婵夘偄鍘栭崚鐨乽tputstream娑擄拷 */
        for(FromFile uploadFile : files){
        	StringBuilder fileEntity = new StringBuilder();
        	fileEntity.append("--");
        	fileEntity.append(BOUNDARY);
        	fileEntity.append("\r\n");
        	fileEntity.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() +
        						"\";filename=\"" + uploadFile.getFileName() + "\"\r\n");
        	fileEntity.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
        	os.write(fileEntity.toString().getBytes());
        	
        	if(uploadFile.getInputStream() != null) {
        		byte[] buffer = new byte[1024];
        		
        		int len = 0;
        		while((len = uploadFile.getInputStream().read(buffer, 0, 1024)) != -1){
        			os.write(buffer, 0, len);
        		}        		
        		uploadFile.getInputStream().close();
        	}else{
        		os.write(uploadFile.getData(), 0, uploadFile.getData().length);
        	}
        	os.write("\r\n".getBytes());
        	os.write("\r\n".getBytes());
        }
        
        /* 娑撳娼伴崣鎴︼拷浣规殶閹诡喚绮ㄩ弶鐔哥垼韫囨绱濈悰銊с仛閺佺増宓佸鑼病缂佹挻娼�  */
        os.write(endLine.getBytes());
        
        /* 瀵拷婵褰傞柅浣稿敶鐎圭懓鍩岄張宥呭閸ｏ拷 */
    	os.flush();
    	
    	boolean bFlag = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        if(reader.readLine().indexOf("200") == -1){
         	/* 鐠囪褰噖eb閺堝秴濮熼崳銊ㄧ箲閸ョ偟娈戦弫鐗堝祦閿涘苯鍨介弬顓☆嚞濮瑰倻鐖滈弰顖氭儊娑擄拷200閿涘苯顩ч弸婊�绗夐弰锟�200閿涘奔鍞悰銊嚞濮瑰倸銇戠拹锟� */
        	if (DEBUG) Log.d(TAG, "Upload request failed.");
        	bFlag = false;
        }
        
        os.close();
        reader.close();
        socket.close();
        
        return bFlag;
    }
    
	/**鑱借伣鑱借伣鑱�
	 *鑱介幓鎰唉閺佺増宓侀崚鐗堟箛閸斺�虫珤
	 * @param鑱絧ath鑱芥稉濠佺炊鐠侯垰绶�(濞夘煉绱伴柆鍨帳娴ｈ法鏁ocalhost閹达拷127.0.0.1鏉╂瑦鐗遍惃鍕熅瀵板嫸绱濋崶鐘辫礋鐎瑰啩绱伴幐鍥ф倻閹靛婧�濡剝瀚欓崳顭掔礉娴ｇ姴褰叉禒銉ゅ▏閻⑩暏ttp://www.itcast.cn閹存潑ttp://192.168.1.10:8080鏉╂瑦鐗遍惃鍕熅瀵板嫭绁寸拠锟�)
	 * @param鑱絧arams鑱界拠閿嬬湴閸欏倹鏆熻伣key娑撳搫寮弫鏉挎倳,value娑撳搫寮弫鏉匡拷锟�
	 * @param鑱絝ile鑱芥稉濠佺炊閺傚洣娆㈣伣鑱借伣鑱借伣
	 */
    public static boolean post(String path, Map<String, String> params,
            FromFile file) throws Exception {
        return post(path, params, new FromFile[]{file});
    }
    
	/**鑱借伣鑱借伣鑱�
	 *鑱介幓鎰唉閺佺増宓侀崚鐗堟箛閸斺�虫珤
	 * @param鑱絧ath鑱芥稉濠佺炊鐠侯垰绶�
	 * @param鑱絝ile鑱芥稉濠佺炊閺傚洣娆�
	 */
    public static boolean post(String path, FromFile file)
    					throws Exception {
        return post(path, null, new FromFile[]{file});
    }
}
