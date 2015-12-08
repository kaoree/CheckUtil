package com.nationpowergrid.mobileoperation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

public class Utils {

	/** 检查计划制定的供电单位编号 */
	public static final String orgNo = "144";
	/** 离线地图存放路径 */
	private static final String OFFLINE_MAP_LOCATION = Environment
			.getExternalStorageDirectory().getPath() + "/BaiduMapSDK/vmp/h/";

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString().toLowerCase();
	}

	// 当前的网络状况
	public static boolean validateInternet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 读取assets中的离线地图文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static void getFromAssets(Context context,String fileName) {

		String filepath = OFFLINE_MAP_LOCATION + fileName;

		try {

			// 创建路径
			File file = new File(OFFLINE_MAP_LOCATION);
			if (!file.exists()) {
				file.mkdirs();
			}

			if (!new File(filepath).exists()) {
				// 写入文件
				InputStream in = context.getResources().getAssets().open(fileName);
				FileOutputStream out = new FileOutputStream(filepath);

				OutputStreamWriter outputWriter = new OutputStreamWriter(out,
						"utf-8");
				BufferedWriter bufWriter = new BufferedWriter(outputWriter);

				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				bufWriter.flush();
				bufWriter.close();
				//Toast.makeText(context, "下载离线地图成功啦", Toast.LENGTH_SHORT).show();
			} else {
				File file1=new File(filepath);
				file1.delete();
				InputStream in = context.getResources().getAssets().open(fileName);
				FileOutputStream out = new FileOutputStream(filepath);

				OutputStreamWriter outputWriter = new OutputStreamWriter(out,
						"utf-8");
				BufferedWriter bufWriter = new BufferedWriter(outputWriter);

				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				bufWriter.flush();
				bufWriter.close();
				//Toast.makeText(context, "下载离线地图成功", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}
	}

	// 将文本转换成标准的时间格式
	public static String parseDate(String year1, String month1) {
		String time = "";
		if (year1 != null && month1 != null) {
			int year = 0;
			int month = 0;
			try {
				year = Integer.parseInt(year1);
				month = Integer.parseInt(month1);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				return "";

			}
			GregorianCalendar date = new GregorianCalendar(year, month, 1);
			Date convert = date.getTime();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
			time = simpleDateFormat.format(convert);
		}

		return time;
	}

	public static String[] spliteDate(String date) {
		if (date.contains("-")) {
			return date.split("-");
		}
		return null;
	}

	/**
	 * base64加密
	 * 
	 * @param before_str
	 * @return
	 */
	public static String base64Encryption(String before_str) {

		String after_str = null;

		// 随机生成5位数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 5; i++) {

			String index = String.valueOf(Math.abs(random.nextInt()) % 10);

			sb.append(index);

		}

		after_str = before_str + " " + sb;

		after_str = Base64.encodeToString(after_str.getBytes(), Base64.NO_WRAP);

		return after_str;

	}

	/**
	 * 登录base64加密
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static String code(String username, String password) {
		String after_str = null;

		after_str = username + " " + password;

		after_str = Base64.encodeToString(after_str.getBytes(), Base64.NO_WRAP);

		return after_str;
	}

}
