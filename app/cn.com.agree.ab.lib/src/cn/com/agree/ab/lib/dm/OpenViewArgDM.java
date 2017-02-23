package cn.com.agree.ab.lib.dm;

import java.util.Map;

public class OpenViewArgDM extends BasicDM {

	private static final long serialVersionUID = 2228833991846758897L;
	
	private Map<String, String> importVar;
	
	private String[] exportNames;
	
	private String title = "";
	
	private boolean isWindow = false;
	
	private int windowX = -1;
	
	private int windowY = -1;
	
	private int windowHeight = 0;
	
	private int windowWidth  = 0;

	public Map<String, String> getImportVar() {
		return importVar;
	}

	public void setImportVar(Map<String, String> importVar) {
		this.importVar = importVar;
	}

	public String[] getExportNames() {
		return exportNames;
	}

	public void setExportNames(String[] exportNames) {
		this.exportNames = exportNames;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isWindow() {
		return isWindow;
	}

	public void setWindow(boolean isWindow) {
		this.isWindow = isWindow;
	}

	public int getWindowX() {
		return windowX;
	}

	public void setWindowX(int windowX) {
		this.windowX = windowX;
	}

	public int getWindowY() {
		return windowY;
	}

	public void setWindowY(int windowY) {
		this.windowY = windowY;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

}
