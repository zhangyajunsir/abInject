package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_trade_post", primaryKey = {"id"})
public class TradePostEntity extends EntityDM {
	private static final long serialVersionUID = -6686487080756300860L;

	@Mapping(table = "ab_trade_post", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_trade_post", columns = {"trade_id"}, requisite = true)
	private Integer tradeId;
	@Mapping(table = "ab_trade_post", columns = {"comm_id"}, requisite = true)
	private Integer commId;
	@Mapping(table = "ab_trade_post", columns = {"post_id"}, requisite = true)
	private Integer postId;
	@Mapping(table = "ab_trade_post", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_trade_post", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_post", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTradeId() {
		return tradeId;
	}
	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}
	public Integer getCommId() {
		return commId;
	}
	public void setCommId(Integer commId) {
		this.commId = commId;
	}
	public Integer getPostId() {
		return postId;
	}
	public void setPostId(Integer postId) {
		this.postId = postId;
	}
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	public String getLastModifyUser() {
		return lastModifyUser;
	}
	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}
	public Date getLastModifyDate() {
		return lastModifyDate;
	}
	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}
	
}
