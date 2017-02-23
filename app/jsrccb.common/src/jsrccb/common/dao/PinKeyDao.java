package jsrccb.common.dao;

import jsrccb.common.dao.entity.PinKeyEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface PinKeyDao  extends EntityDao<PinKeyEntity>{

	public PinKeyEntity getPinKey(String devId);
	
	public void updatePinKey(PinKeyEntity pinKeyEntity);
}
