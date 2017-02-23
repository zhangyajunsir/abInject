package jsrccb.common.dm;


public class PinDM extends PinKeyDM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6239276568942744946L;
	private String GM32MW;//国密32位密文
	private String GG16MW;//国际16位密文
	public String getGM32MW() {
		return GM32MW;
	}
	public void setGM32MW(String gM32MW) {
		GM32MW = gM32MW;
	}
	public String getGG16MW() {
		return GG16MW;
	}
	public void setGG16MW(String gG16MW) {
		GG16MW = gG16MW;
	}
}
