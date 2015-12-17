# CheckUtil
android文本框校验和对象空值校验
## Example
 ```Java
  @CheckNullable(tipValue = "计划安排预试时间不能为空")
   private  EditText ed1;
  @CheckNullable(tipValue = "容量不能为空", fieldType = Type.Number)
   private  EditText ed2;
	 public void onCreate(Bundle savedInstanceState){
	    CheckUtils.Instance().check(this);
	 }
 ```
当ed1的值为空时，提示tipValue里的值<br>
当ed2的值为空时，提示tipValue里的值，当内容不是数字类型的时候，提示输入数字
