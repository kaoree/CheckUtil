package com.nationpowergrid.mobileoperation.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.nationpowergrid.mobileoperation.interfaces.Bind;
import com.nationpowergrid.mobileoperation.interfaces.Inject;
import com.nationpowergrid.mobileoperation.views.SnapView;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InjectUtils {
	public static InjectUtils utils;
	private Context context;
	private SnapView mView;

	private InjectUtils() {

	}

	public static InjectUtils Instance() {
		if (utils == null) {
			utils = new InjectUtils();

		}
		return utils;
	}

	public InjectUtils loadLayout(SnapView view) {
		this.mView = view;
		return this;
	}

	public void inject(Context context) {
		this.context = context;
		Class<?> mClass = context.getClass();
		Field[] fields = mClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].isAnnotationPresent(Inject.class)) {
				if (!fields[i].isAccessible()) {
					fields[i].setAccessible(true);
				}
				try {
					Object entity = fields[i].get(context);
					if (entity != null) {
						injectView(entity, context);
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public boolean parse(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				int flag = (Integer) value;
				if (flag == 1) {
					return true;
				} else {
					return false;
				}
			} else if (value instanceof String) {
				if (value.equals("1")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public String changeTValue(String key) {
		if (key.equals("01")) {
			return "良好";
		} else if (key.equals("02")) {
			return "一般";
		} else if (key.equals("03")) {
			return "差";
		}
		return "";
	}

	public String changeTWValue(String key) {
		if (key.equals("01")) {
			return "已签订";
		} else if (key.equals("02")) {
			return "未签订";
		}
		return "";
	}

	public void injectView(Object entity, Context cxt) {
		Class<?> type = entity.getClass();
		Activity ac = (Activity) cxt;
		Field[] fields = type.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.isAnnotationPresent(Bind.class)) {
				Bind anno = field.getAnnotation(Bind.class);
				int resId = anno.id();
				int[] ids = anno.ids();
				int layoutId = anno.layout();
				int refid=anno.refid();
				String values = anno.values();
			
				
				if (resId != View.NO_ID) {
					android.view.View view = (android.view.View) ((Activity) context)
							.findViewById(resId);
					if (view instanceof ToggleButton) {
						ToggleButton btn = (ToggleButton) view;
						btn.setEnabled(false);
						Boolean flag = (Boolean) parse(getFieldValue(entity,
								field));
						if (flag == Boolean.TRUE) {
							btn.setChecked(true);
							
						} else {
							btn.setChecked(false);
							
						}

					} else if (view instanceof TextView) {
						TextView edt = (TextView) view;
						edt.setEnabled(false);
						String txt = String
								.valueOf(getFieldValue(entity, field));
						if (!txt.equals("null")) {
							if (txt.equals("0")) {
								edt.setText("无");
							} else if (txt.equals("1")) {
								edt.setText("有");
							} else if (values.equals("3")) {
								edt.setText(changeTValue(txt));
							} else if (values.equals("2")) {
								edt.setText(changeTWValue(txt));
							} else {
								edt.setText(txt);
							}

						}

					}
				} else if (ids.length > 0 && ids[0] != View.NO_ID) {
					// for (int j = 0; j < ids.length; j++) {
					// android.view.View view = (android.view.View) ((Activity)
					// context)
					// .findViewById(ids[j]);
					// if (values.length > 0) {
					// if (view instanceof RadioButton) {
					// RadioButton rb = (RadioButton) view;
					// rb.setEnabled(false);
					// String value = (String) getFieldValue(entity,
					// field);
					// if (values[j].equals(value)) {
					// rb.setChecked(true);
					// }
					//
					// }
					// }
					//
					// }
				}

			}
		}

	}

	// 获得class 属性的值
	public Object getFieldValue(Object entity, Field field) {
		Object fieldValue = null;
		Class entityClass = entity.getClass();
		Method getMethod = getColumnGetMethod(entityClass, field);
		if (entity != null) {
			if (getMethod != null) {
				try {
					fieldValue = getMethod.invoke(entity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return fieldValue;
	}

	// 获得类属性的get方法
	public static Method getColumnGetMethod(Class<?> entityType, Field field) {
		String fieldName = field.getName();
		Method getMethod = null;
		if (field.getType() == boolean.class) {
			getMethod = getBooleanColumnGetMethod(entityType, fieldName);
		}
		if (getMethod == null) {
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			try {
				getMethod = entityType.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException e) {
				Log.v("jk", methodName + " not exist");
			}
		}

		if (getMethod == null
				&& !Object.class.equals(entityType.getSuperclass())) {
			return getColumnGetMethod(entityType.getSuperclass(), field);
		}
		return getMethod;
	}

	// 如果属性是bool类型，获取is...方法
	private static Method getBooleanColumnGetMethod(Class<?> entityType,
			final String fieldName) {
		String methodName = "is" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
		if (isStartWithIs(fieldName)) {
			methodName = fieldName;
		}
		try {
			return entityType.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			Log.v("jk", methodName + " not exist");
		}
		return null;
	}

	private static boolean isStartWithIs(final String fieldName) {
		return fieldName != null && fieldName.startsWith("is");
	}
}
