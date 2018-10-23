package com.ms.linuxMonitor.common;

import java.math.BigDecimal;

/**
 * @author hecy
 * @version 1.0
 * @date 2018/10/23
 */
public class CommonUtils {



    public static BigDecimal toBigDecimal(String data) {
        if(data == null || "".equals(data)) {
            return new BigDecimal(0);
        }
        return new BigDecimal(data);
    }


}
