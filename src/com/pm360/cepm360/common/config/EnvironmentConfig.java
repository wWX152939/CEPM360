package com.pm360.cepm360.common.config;

import android.annotation.SuppressLint;
import android.util.Log;

import com.pm360.cepm360.common.net.HttpClientTransmit;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EnvironmentConfig {
	
	@SuppressLint("SdCardPath")
	public static void parseFiles() {
		String serverIp = HttpClientTransmit.getServerIp();
		try {
			File file = new File("/data/data/com.pm360.cepm360/files/connect_ip.txt");
			if (!file.exists()) {
				// 创建文件，导入默认内容
				FileUtils.createOrExistsFile(file);
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(file));
					String ip = "connectIP=" + serverIp + "\n";
					writer.write(ip);
					String level = "logLevel=" + "v" + "\n";
					writer.write(level);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			int flag = 0;
			final int connectIPFlag = 0x1;
			final int logLevelFlag = 0x2;
			String lineString;
			while ((lineString = bufferedReader.readLine()) != null) {
				if (lineString.contains("connectIP")) {
					String[] connectIPs = lineString.split("=");
					if (connectIPs != null && connectIPs.length > 1 && connectIPs[1] != null) {
						serverIp = connectIPs[1];
						flag += connectIPFlag;
					}
				}
				if (lineString.contains("logLevel")) {
					String[] logLevels = lineString.split("=");
					if (logLevels != null && logLevels.length > 1 && logLevels[1] != null) {
						String logLevel = logLevels[1];
						int defaultValue = 0;
						switch (logLevel) {
						case "v":
							defaultValue = Log.VERBOSE;
							break;
						case "d":
							defaultValue = Log.DEBUG;
							break;
						case "i":
							defaultValue = Log.INFO;
							break;
						case "w":
							defaultValue = Log.WARN;
							break;
						case "e":
							defaultValue = Log.ERROR;
							break;
						}
						if (defaultValue != 0) {
							flag += logLevelFlag;
						}
						LogUtil.setCurLogLevel(defaultValue);
					}
				}
			}
			bufferedReader.close();
			if (flag != (logLevelFlag + connectIPFlag)) {
				// 文件内容缺失补填缺失内容
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(file));
					if ((flag & connectIPFlag) != connectIPFlag) {
						String ip = "connectIP=" + serverIp + "\n";
						writer.write(ip);
					}

					if ((flag & logLevelFlag) != logLevelFlag) {
						String level = "logLevel=" + "v" + "\n";
						writer.write(level);
					}
					
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (serverIp != HttpClientTransmit.getServerIp()) {
			HttpClientTransmit.resetServerIp(serverIp);
		}
	}
}
