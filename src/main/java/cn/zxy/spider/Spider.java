package cn.zxy.spider;

import cn.zxy.config.ConfigLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.sun.org.apache.bcel.internal.generic.BIPUSH;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Bidi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Silence
 * @Date 2017/12/11
 */
public abstract class Spider {
    private static final  String USD_RATE_URL = "https://api.fixer.io/latest?base=USD";

    //获取数据失败重试3次
    protected static final Retryer<String> RETRYER_TIME_OUT = RetryerBuilder.<String>newBuilder()
            .withWaitStrategy(WaitStrategies.noWait())
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .retryIfResult(x -> (x == null || x.trim().length() == 0))
            .build();

    protected BigDecimal getUsdRate() {
        String content = null;
        try {
            content = RETRYER_TIME_OUT.call(() -> HttpUtil.doGet(USD_RATE_URL, null));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }

        if (content == null) {
            System.out.println("本次获取数据失败");
            return null;
        }

        JSONObject jsonObject = JSON.parseObject(content);
        return jsonObject.getJSONObject("rates").getBigDecimal("CNY");
    }


    public abstract Map<String, Double> getCoinDatas();
}
