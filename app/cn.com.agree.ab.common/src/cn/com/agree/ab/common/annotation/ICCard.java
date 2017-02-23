package cn.com.agree.ab.common.annotation;

public @interface ICCard {
	// 是否必输
	boolean must()  default false;
	// 组件名:栏位名
	String[] result() default {};
}
