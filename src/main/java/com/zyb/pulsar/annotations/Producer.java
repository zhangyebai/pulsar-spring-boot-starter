package com.zyb.pulsar.annotations;


import java.lang.annotation.*;

/**
 * javadoc Producer
 * <p>
 *      pulsar message producer
 * <p>
 * @author zhang yebai
 * @date 2021/3/10 4:18 PM
 * @version 1.0.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Producer {
}
