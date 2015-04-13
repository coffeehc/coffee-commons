/**
 * 
 */
package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.TypeLiteral;

/**
 * @author coffeehc@gmail.com
 * create By 2015年1月31日
 */
public interface Transport {

    <T> T in(InputStream in, Class<T> type) throws IOException;
    
    <T> T in(InputStream in, TypeLiteral<T> type) throws IOException;
    
    <T> void out(OutputStream out, Class<T> type, T data) throws IOException;
    /**
     * 
     * @return HTTP内容的 mime type
     */
    String contentType();
}
