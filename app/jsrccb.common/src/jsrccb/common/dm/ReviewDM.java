package jsrccb.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class ReviewDM extends BasicDM {
	private static final long serialVersionUID = 6290968334063304989L;
	
	public static enum ReviewStatus {
		
	}
	
	private ReviewStatus reviewStatus;

	public ReviewStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(ReviewStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

}
