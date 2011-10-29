package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryAsyncTest extends AbstractTest<AQueryTestActivity> {

	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public AQueryAsyncTest() {		
		super(AQueryTestActivity.class);
    }

	
	private void done(String url, Object result, AjaxStatus status){
		
		this.url = url;
		this.result = result;
		this.status = status;

		log("done", result);
		
		checkStatus(status);
		
		done();
		
	}
	
	private void checkStatus(AjaxStatus status){
		
		AQUtility.debug("redirect", status.getRedirect());
		AQUtility.debug("time", status.getTime());
		AQUtility.debug("response", status.getCode());
		
		assertNotNull(status);
		
		assertNotNull(status.getRedirect());
		
		if(result != null && status.getSource() == AjaxStatus.NETWORK){
			assertNotNull(status.getClient());
		}
		
		assertNotNull(status.getTime());
		assertNotNull(status.getMessage());
		
		
		
		
	}
	

	
	//Test: public <K> T ajax(AjaxCallback<K> callback)
	public void testAjaxAdvance() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		cb.url(url).type(JSONObject.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, AjaxCallback<K> callback)
	public void testAjaxCallback() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
			
        aq.ajax(url, JSONObject.class, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, Object handler, String callback)
	public void testAjaxHandler() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.ajax(url, JSONObject.class, this, "jsonCb");
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: <K> T ajax(String url, Map<String, Object> params, Class<K> type, AjaxCallback<K> callback)
	public void testAjaxPost(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	done(url, jo, status);
               
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("results"));
        
	}
	
	//Test: public <K> T ajax(String url, Map<String, Object> params, Class<K> type, Object handler, String callback)
	public void testAjaxPostHandler(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.ajax(url, params, JSONObject.class, this, "jsonCb");
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("results"));
        
	}
	
	//Test: public <K> T ajax(String url, Class<K> type, long expire, AjaxCallback<K> callback)
	public void testAjaxCache() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
			
        aq.ajax(url, JSONObject.class, 15 * 60 * 1000, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
       
		File cached = aq.getCachedFile(url);
		assertTrue(cached.exists());
		assertTrue(cached.length() > 100);
		
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, long expire, Object handler, String callback)
	public void testAjaxCacheHandler() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.ajax(url, JSONObject.class, 15 * 60 * 1000, this, "jsonCb");
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	public void jsonCb(String url, JSONObject jo, AjaxStatus status){
				
		done(url, jo, status);
	}
	
	//Test: public T cache(String url, long expire)
	public void testCache(){
		
		
		AQUtility.cleanCache(AQUtility.getCacheDir(getActivity()), 0, 0);
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		
		File file = aq.getCachedFile(url);
		
		assertNull(file);
		
		aq.cache(url, 0);
		
		waitAsync();
		
		AQUtility.debugWait(2000);
		
		file = aq.getCachedFile(url);
		
		assertNotNull(file);
		
		
	}
	
	public void test301(){
		
		String url = "http://jigsaw.w3.org/HTTP/300/301.html";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String html, AjaxStatus status) {
				
				done(url, html, status);
				
			}
			
		};
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        String html = (String) result;
        
        assertNotNull(html);       
        
        assertEquals("http://jigsaw.w3.org/HTTP/300/Overview.html", status.getRedirect());
        
	}
	
	
	public void test307(){
		
		String url = "http://jigsaw.w3.org/HTTP/300/307.html";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String html, AjaxStatus status) {
				
				done(url, html, status);
				
			}
			
		};
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        String html = (String) result;
        
        assertNotNull(html);       
        
        assertEquals("http://jigsaw.w3.org/HTTP/300/Overview.html", status.getRedirect());
        
        
		
	}
	
	public void testTransformError(){
		
		
		String url = "http://www.google.com";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(AjaxStatus.TRANSFORM_ERROR, status.getCode());
		
	}
	
	public void test404Error(){
		
		
		String url = "http://androidquery.appspot.com/test/fake";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(404, status.getCode());
		
	}
	
	
	public void testNetworkError(){
		
		
		String url = "httpd://wrongschema";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(AjaxStatus.NETWORK_ERROR, status.getCode());
		
	}
	
	public void testInvalidate(){
		
		testAjaxCache();
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        aq.invalidate(url);
		
        File cached = aq.getCachedFile(url);
        assertNull(cached);
        
		
	}
	
}