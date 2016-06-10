package com.exactprosystems.ticker.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Use with registering channels messages in MessageFactory
 * 
 * @author dmitry.zavodchikov
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelsMessage {

}
