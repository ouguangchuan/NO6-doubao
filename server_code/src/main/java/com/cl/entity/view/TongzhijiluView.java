package com.cl.entity.view;

import com.cl.entity.TongzhijiluEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class TongzhijiluView extends TongzhijiluEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public TongzhijiluView(){
	}

	public TongzhijiluView(TongzhijiluEntity tongzhijiluEntity){
		try {
			org.springframework.beans.BeanUtils.copyProperties(tongzhijiluEntity, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
