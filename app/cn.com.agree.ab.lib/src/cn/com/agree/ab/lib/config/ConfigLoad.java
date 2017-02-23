package cn.com.agree.ab.lib.config;

import java.io.InputStream;
import java.net.URL;

/**
 * 加载接口
 * @author 
 *
 */
public interface ConfigLoad<T> {

    T load(URL url, InputStream is) throws Exception;

}

