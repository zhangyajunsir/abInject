package jsrccb.common.dm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.agree.ab.lib.dm.BasicDM;

public class PostCfgDM extends BasicDM {
	private static final long serialVersionUID = 1L;
	
	private Map<String, List<PostCfgItem>> tradeCfgPosts = new HashMap<String, List<PostCfgItem>>();
	
	public synchronized void init(InputStream cfgInputStream) throws IOException {
		if (!tradeCfgPosts.isEmpty())
			tradeCfgPosts.clear();
		BufferedReader input = new BufferedReader(new InputStreamReader(cfgInputStream));
		String line = null;  
		while((line = input.readLine()) != null) {
			if (line.length() <= 8)
				continue;
			String tradeCode = line.substring(0, 6).trim();
			String funcValue = line.substring(6, 8).trim();
			String indexPost = line.substring(8).trim();
			List<PostCfgItem> postCfgItems = tradeCfgPosts.get(tradeCode);
			if (postCfgItems == null) {
				postCfgItems = new ArrayList<PostCfgItem>();
				tradeCfgPosts.put(tradeCode, postCfgItems);
			}
			List<Integer> posts = new ArrayList<Integer>();
			char[] indexPosts   = indexPost.toCharArray();
			for (int i=0; i<192 && i<indexPosts.length ; i++) {
				if (indexPosts[i] == '1') {
					posts.add(i+1);	// 数据库里的岗位ID正好是这边的数组坐标
				}
			}
			postCfgItems.add(new PostCfgItem(tradeCode, funcValue, posts));
		}
	}
	
	public Map<String, List<PostCfgItem>> getTradeCfgPosts() {
		return tradeCfgPosts;
	}
	
	public class PostCfgItem {
		private String tradeCode;
		private String funcValue;
		private List<Integer> posts;
		
		private PostCfgItem(String tradeCode, String funcValue,  List<Integer> posts){
			this.tradeCode = tradeCode;
			this.funcValue = funcValue;
			this.posts     = posts;
		}
		
		public String getTradeCode() {
			return tradeCode;
		}
		public String getFuncValue() {
			return funcValue;
		}
		public List<Integer> getPosts() {
			return posts;
		}
	}
}
