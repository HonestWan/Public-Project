package com.demo.model;
/**
 * 城市类
 * @author pactera
 *
 */
public class City {
	private String code;
	private String name;
	private String href;
	
	public City(String code, String name, String href) {
		this.code = code;
		this.name = name;
		this.href = href;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
}
